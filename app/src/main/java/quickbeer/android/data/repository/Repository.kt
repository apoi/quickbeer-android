package quickbeer.android.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import quickbeer.android.data.state.State
import quickbeer.android.network.result.ApiResult

/**
 * Repository binds [Store] and [Api] together to a data source that handles
 * data fetching and persisting.
 */
abstract class Repository<in K, V> {

    /**
     * Returns the current state without fetching.
     */
    suspend fun get(key: K): State<V> {
        return withContext(Dispatchers.IO) {
            getLocal(key)
                ?.let { State.Success(it) }
                ?: State.Empty
        }
    }

    /**
     * Returns a stream of data with fetching if current value is invalid.
     */
    fun getStream(key: K, validator: Validator<V>): Flow<State<V>> {
        return flow {
            emit(State.Loading)

            // Invalid value triggers fetch. It must not trigger clearing of the
            // value as the value may still be valid for another consumer with
            // a different validator.
            val isValid = getLocal(key)?.let(validator::validate) == true

            if (!isValid) {
                when (val response = fetchRemote(key)) {
                    is ApiResult.Success -> {
                        if (response.value != null) {
                            // Response is persisted to be emitted later
                            persist(response.value)
                        } else {
                            // Empty states don't go through persistence layer
                            emit(State.Empty)
                        }
                    }
                    // State can be expanded for more detailed error types
                    is ApiResult.HttpError -> emit(State.Error(response.error))
                    is ApiResult.NetworkError -> emit(State.Error(response.error))
                    is ApiResult.UnknownError -> emit(State.Error(response.error))
                }
            }

            // Emit current and future values. The values need to be still validated
            // as otherwise this may emit the existing value in case of failed fetch.
            emitAll(
                getLocalStream(key)
                    .filter { validator.validate(it) }
                    .map { State.Success(it) }
            )
        }.flowOn(Dispatchers.IO)
    }

    protected abstract suspend fun persist(value: V)

    protected abstract suspend fun getLocal(key: K): V?

    protected abstract fun getLocalStream(key: K): Flow<V>

    protected abstract suspend fun fetchRemote(key: K): ApiResult<V>
}

/**
 * Special case of Repository that can only contain a single value.
 */
abstract class SingleRepository<V> {

    private val repository = object : Repository<Int, V>() {

        override suspend fun persist(value: V) {
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

    suspend fun get(): State<V> {
        return repository.get(0)
    }

    fun getStream(validator: Validator<V>): Flow<State<V>> {
        return repository.getStream(0, validator)
    }

    protected abstract suspend fun persist(value: V)

    protected abstract suspend fun getLocal(): V?

    protected abstract fun getLocalStream(): Flow<V>

    protected abstract suspend fun fetchRemote(): ApiResult<V>
}
