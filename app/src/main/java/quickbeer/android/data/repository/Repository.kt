package quickbeer.android.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import quickbeer.android.data.state.State
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.exception.AppException.RepositoryKeyEmpty
import quickbeer.android.util.exception.AppException.RepositoryKeyInvalid
import quickbeer.android.util.ktx.takeUntil

/**
 * Repository binds [Store] and [Api] together to a data source that handles
 * data fetching and persisting.
 */
abstract class Repository<K, V> {

    /**
     * Returns a stream of data with fetching if current value is invalid.
     */
    fun getStream(key: K, validator: Validator<V>): Flow<State<V>> {
        return flow {
            // Start with local values with the acknowledgement the values may still
            // be updated. User chooses if to use already loading values or not.
            val local = getLocal(key)
            emit(State.Loading(local))

            // Invalid value triggers fetch. It must not trigger clearing of the
            // value as the value may still be valid for another consumer with
            // a different validator.
            val isValid = validator.validate(local)

            if (!isValid) {
                when (val response = fetchRemote(key)) {
                    is ApiResult.Success -> {
                        if (response.value != null && validator.validate(response.value)) {
                            // Response is persisted to be emitted later
                            persist(key, response.value)
                        } else {
                            // Empty states don't go through persistence layer
                            emit(State.Empty)
                        }
                    }
                    // State can be expanded for more detailed error types
                    is ApiResult.HttpError -> emit(State.Error(response.cause))
                    is ApiResult.NetworkError -> emit(State.Error(response.cause))
                    is ApiResult.UnknownError -> emit(State.Error(response.cause))
                }
            }

            // Emit current and future values. The values need to be still validated
            // as otherwise this may emit the existing value in case of failed fetch.
            emitAll(
                getLocalStream(key)
                    .filter { validator.validate(it) }
                    .map { State.from(it) }
            )
        }.flowOn(Dispatchers.IO)
    }

    // TODO something goes wrong with this.
    fun getStream2(key: K, validator: Validator<V>): Flow<State<V>> {
        return getStream(flow { emit(key) }, null, validator, 0)
    }

    /**
     * Returns a stream of data that reacts to key flow changes by cancelling old requests and
     * starting again from Loading state.
     */
    fun getStream(
        keyFlow: Flow<K>,
        keyValidator: KeyValidator<K>?,
        validator: Validator<V>,
        debounceRemote: Long = 0
    ): Flow<State<V>> {
        // Only consider distinct keys.
        val distinctKey = keyFlow
            .distinctUntilChanged()

        // Key may be invalid. This error needs to be emitted from Repository as it must cancel
        // any active flows from previous keys.
        val errorFlow = distinctKey
            .filter { keyValidator?.isValid(it) == false }
            .mapLatest {
                when {
                    keyValidator?.isEmpty(it) == true -> State.Error(RepositoryKeyEmpty)
                    keyValidator?.isValid(it) == false -> State.Error(RepositoryKeyInvalid)
                    else -> error("Unexpected key")
                }
            }

        // Flow containing current value and its validity.
        val stateFlow = distinctKey
            .filter { keyValidator == null || keyValidator.isValid(it) }
            .mapLatest {
                val current = getLocal(it)
                FlowState(it, current, validator.validate(current))
            }

        // API request flow. This flow can be debounced if repository expects multiple keys in
        // quick succession, such as during search input.
        val remoteFlow = stateFlow
            .filter { !it.isValid }
            .flatMapLatest { state ->
                // Separate flow for cancellability: cancel the delay-fetch flow immediately on new
                // key as the value isn't needed any more. This should have the same behaviour as
                // debounce has, with explicit cancellation.
                flow { emit(state) }
                    .onEach { delay(debounceRemote) }
                    .flatMapLatest { fetch(it.key, validator) }
                    .takeUntil(distinctKey.drop(1))
            }

        // Local flow emits Loading state with currently cached results while waiting for the API
        // flow to complete, or Success state if the local data is already valid.
        val localFlow = stateFlow
            .flatMapLatest { state ->
                flow { emit(state) }
                    .flatMapLatest { getFuzzyLocalStream(state.key) }
                    .take(1)
                    .flatMapLatest {
                        flow {
                            // Always emit Loading first for consistency
                            emit(State.Loading(it))
                            if (state.isValid) emit(State.Success(it))
                        }
                    }
                    // Cancel if remote or error emits. In this case the Loading state
                    // with local value is received too late.
                    .takeUntil(merge(errorFlow, remoteFlow))
            }

        return merge(errorFlow, remoteFlow, localFlow)
            .flowOn(Dispatchers.IO)
    }

    private fun fetch(key: K, validator: Validator<V>): Flow<State<V>> {
        return flow {
            when (val response = fetchRemote(key)) {
                is ApiResult.Success -> {
                    if (response.value != null && validator.validate(response.value)) {
                        // Response is persisted to be emitted later
                        persist(key, response.value)
                    } else {
                        // Empty states don't go through persistence layer
                        emit(State.Empty)
                    }
                }
                // State can be expanded for more detailed error types
                is ApiResult.HttpError -> emit(State.Error(response.cause))
                is ApiResult.NetworkError -> emit(State.Error(response.cause))
                is ApiResult.UnknownError -> emit(State.Error(response.cause))
            }

            // Emit current and future values. The values need to be still validated
            // as otherwise this may emit the existing value in case of failed fetch.
            emitAll(
                getFuzzyLocalStream(key)
                    .distinctUntilChanged()
                    .filter { validator.validate(it) }
                    .map { State.from(it) }
            )
        }
    }

    protected abstract suspend fun persist(key: K, value: V)

    protected abstract suspend fun getLocal(key: K): V?

    protected abstract fun getLocalStream(key: K): Flow<V>

    protected open fun getFuzzyLocalStream(key: K) = getLocalStream(key)

    protected abstract suspend fun fetchRemote(key: K): ApiResult<V>

    inner class FlowState(val key: K, val value: V?, val isValid: Boolean)

    interface KeyValidator<in K> {
        fun isEmpty(key: K): Boolean
        fun isValid(key: K): Boolean
    }
}

/**
 * Special case of Repository that can only contain a single value.
 */
abstract class SingleRepository<V> {

    private val repository = object : Repository<Int, V>() {

        override suspend fun persist(key: Int, value: V) {
            return this@SingleRepository.persist(value)
        }

        override suspend fun getLocal(key: Int): V? {
            return this@SingleRepository.getLocal()
        }

        override fun getLocalStream(key: Int): Flow<V> {
            return this@SingleRepository.getLocalStream()
        }

        override suspend fun fetchRemote(key: Int): ApiResult<V> {
            return this@SingleRepository.fetchRemote()
        }
    }

    fun getStream(validator: Validator<V>): Flow<State<V>> {
        return repository.getStream(0, validator)
    }

    protected abstract suspend fun persist(value: V)

    protected abstract suspend fun getLocal(): V?

    protected abstract fun getLocalStream(): Flow<V>

    protected abstract suspend fun fetchRemote(): ApiResult<V>
}
