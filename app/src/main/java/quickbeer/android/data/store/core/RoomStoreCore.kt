package quickbeer.android.data.store.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import quickbeer.android.data.store.StoreCore
import quickbeer.android.util.Mapper

/**
 * Shared implementation for StoreCores with Room as the backing store.
 *
 * @param <K> Type of keys.
 * @param <V> Type of values. Value is the domain model.
 * @param <E> Type of entities. Entity is the database representation of value.
 */
abstract class RoomStoreCore<K : Any, V : Any, E : Any>(
    private val entityMapper: Mapper<V, E>,
    private val coreProxy: StoreCore<K, E>
) : StoreCore<K, V> {

    private val putStream = MutableSharedFlow<V>()

    private val deleteStream = MutableSharedFlow<K>()

    override suspend fun get(key: K): V? {
        return coreProxy.get(key)?.let(entityMapper::mapTo)
    }

    override suspend fun get(keys: List<K>): List<V> {
        return coreProxy.get(keys).map(entityMapper::mapTo)
    }

    override fun getStream(key: K): Flow<V> {
        return coreProxy.getStream(key)
            .distinctUntilChanged()
            .map(entityMapper::mapTo)
    }

    override suspend fun getAll(): List<V> {
        return coreProxy.getAll()
            .map(entityMapper::mapTo)
    }

    override fun getAllStream(): Flow<List<V>> {
        return coreProxy.getAllStream()
            .map { it.map(entityMapper::mapTo) }
    }

    override suspend fun getKeys(): List<K> {
        return coreProxy.getKeys()
    }

    override fun getKeysStream(): Flow<List<K>> {
        return coreProxy.getKeysStream()
    }

    override suspend fun put(key: K, value: V): V? {
        return coreProxy.put(key, entityMapper.mapFrom(value))
            ?.let(entityMapper::mapTo)
            ?.also { putStream.emit(it) }
    }

    override suspend fun put(items: Map<K, V>): List<V> {
        return coreProxy.put(items.mapValues { entityMapper.mapFrom(it.value) })
            .map(entityMapper::mapTo)
            .onEach { putStream.emit(it) }
    }

    override fun getPutStream(): Flow<V> {
        return putStream
    }

    override suspend fun delete(key: K): Boolean {
        return coreProxy.delete(key)
            .also { if (it) deleteStream.emit(key) }
    }

    override fun getDeleteStream(): Flow<K> {
        return deleteStream
    }
}
