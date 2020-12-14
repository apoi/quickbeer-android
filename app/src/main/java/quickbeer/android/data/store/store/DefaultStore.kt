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
open class DefaultStore<K, V>(private val core: StoreCore<K, V>) : Store<K, V> {

    override suspend fun get(key: K): V? {
        return withContext(Dispatchers.IO) {
            core.get(key)
        }
    }

    override fun getStream(key: K): Flow<V> {
        return core.getStream(key)
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getKeys(): List<K> {
        return core.getKeys()
    }

    override fun getKeysStream(): Flow<List<K>> {
        return core.getKeysStream()
    }

    override suspend fun put(key: K, value: V): Boolean {
        return withContext(Dispatchers.IO) {
            core.put(key, value) != null
        }
    }

    override suspend fun delete(key: K): Boolean {
        return withContext(Dispatchers.IO) {
            core.delete(key)
        }
    }
}
