package quickbeer.android.data.store.store

import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.store.Store
import quickbeer.android.data.store.StoreCore

/**
 * Default implementation of a store.
 *
 * @param <K> Type of keys.
 * @param <V> Type of values.
 */
open class DefaultStore<K, V>(private val core: StoreCore<K, V>) : Store<K, V> {

    override suspend fun get(): List<V> {
        return core.getAll()
    }

    override fun getStream(): Flow<List<V>> {
        return core.getAllStream()
    }

    override suspend fun get(key: K): V? {
        return core.get(key)
    }

    override fun getStream(key: K): Flow<V> {
        return core.getStream(key)
    }

    override suspend fun getKeys(): List<K> {
        return core.getKeys()
    }

    override fun getKeysStream(): Flow<List<K>> {
        return core.getKeysStream()
    }

    override suspend fun put(key: K, value: V): Boolean {
        return core.put(key, value) != null
    }

    override suspend fun delete(key: K): Boolean {
        return core.delete(key)
    }
}
