package quickbeer.android.data.repository.repository

import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.repository.Repository
import quickbeer.android.data.store.Store
import quickbeer.android.network.result.ApiResult

open class DefaultRepository<in K, V>(
    private val store: Store<K, V>,
    private val fetcher: suspend (K) -> ApiResult<V>
) : Repository<K, V>() {

    override suspend fun persist(value: V) {
        store.put(value)
    }

    override suspend fun getLocal(key: K): V? {
        return store.get(key)
    }

    override fun getLocalStream(key: K): Flow<V> {
        return store.getStream(key)
    }

    override suspend fun fetchRemote(key: K): ApiResult<V> {
        return fetcher.invoke(key)
    }
}
