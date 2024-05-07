package quickbeer.android.data.store.core

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import quickbeer.android.data.store.Merger
import quickbeer.android.data.store.StoreCore

/**
 * StoreCore with memory as the backing store. This store does not persist anything across
 * application restarts.
 *
 * @param <K> Type of keys.
 * @param <V> Type of values.
 */
class MemoryStoreCore<K : Any, V : Any>(
    private val merger: Merger<V>
) : StoreCore<K, V> {

    // All values in the store
    private val cache = ConcurrentHashMap<K, V>()

    // Flow of all updated values
    private val putStream = MutableSharedFlow<V>()

    // Flow of all deleted keys
    private val deleteStream = MutableSharedFlow<K>()

    // Listeners for given keys
    private val listeners = ConcurrentHashMap<K, MutableSharedFlow<V>>()

    // Guard for synchronizing all writing methods
    private val lock = ReentrantLock()

    override suspend fun get(key: K): V? {
        return cache[key]
    }

    override suspend fun get(keys: List<K>): List<V> {
        return keys.mapNotNull { cache[it] }
    }

    override fun getStream(key: K): Flow<V> {
        return lock.withLock {
            getOrCreateChannel(key)
        }
    }

    override suspend fun getAll(): List<V> {
        return cache.values.toList()
    }

    override fun getAllStream(): Flow<List<V>> {
        return putStream
            .combine(deleteStream) { _, _ -> Unit }
            .map { getAll() }
    }

    override suspend fun getKeys(): List<K> {
        return cache.keys().toList()
    }

    override fun getKeysStream(): Flow<List<K>> {
        return putStream
            .combine(deleteStream) { _, _ -> Unit }
            .map { getKeys() }
    }

    override suspend fun put(key: K, value: V): V? {
        lock.lock()

        val (newValue, valuesDiffer) = mergeValues(cache[key], value, merger)
        if (!valuesDiffer) {
            // Data is already up to date
            lock.unlock()
            return null
        }

        cache[key] = newValue
        putStream.emit(newValue)
        listeners[key]?.emit(newValue)

        lock.unlock()
        return newValue
    }

    override suspend fun put(items: Map<K, V>): List<V> {
        return items.mapNotNull { (key, value) -> put(key, value) }
    }

    override fun getPutStream(): Flow<V> {
        return putStream
    }

    override suspend fun delete(key: K): Boolean {
        lock.lock()

        val deleted = cache.remove(key) != null
        if (deleted) deleteStream.emit(key)

        lock.unlock()
        return deleted
    }

    override suspend fun deleteAll(): Boolean {
        lock.lock()

        val keys = cache.keys
        cache.clear()
        keys.forEach { deleteStream.emit(it) }

        lock.unlock()
        return keys.isNotEmpty()
    }

    override fun getDeleteStream(): Flow<K> {
        return deleteStream
    }

    private fun getOrCreateChannel(key: K): MutableSharedFlow<V> {
        // Just return if channel already exists
        listeners[key]?.let {
            return@getOrCreateChannel it
        }

        // Doesn't exist, create new channel and init if value exist
        return MutableSharedFlow<V>(replay = 1).also { flow ->
            cache[key]?.let(flow::tryEmit)
            listeners.putIfAbsent(key, flow)
        }
    }
}
