package quickbeer.android.data.store.store

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import quickbeer.android.data.store.Store
import quickbeer.android.data.store.StoreCore

/**
 * Default implementation of a store.
 *
 * @param <K> Type of keys.
 * @param <V> Type of values.
 */
open class DefaultStore<in K, V>(
    private val core: StoreCore<K, V>,
    private val getKey: (V) -> K
) : Store<K, V> {

    override suspend fun get(key: K): V? {
        return withContext(Dispatchers.IO) {
            core.get(key)
        }
    }

    override fun getStream(key: K): Flow<V> {
        return core.getStream(key)
            .flowOn(Dispatchers.IO)
    }

    override suspend fun put(value: V): Boolean {
        return withContext(Dispatchers.IO) {
            core.put(getKey(value), value) != null
        }
    }

    override suspend fun delete(key: K): Boolean {
        return withContext(Dispatchers.IO) {
            core.delete(key)
        }
    }
}
