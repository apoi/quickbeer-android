package quickbeer.android.data.store.core

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import quickbeer.android.data.store.Merger
import quickbeer.android.data.store.StoreCore
import timber.log.Timber

/**
 * StoreCore with memory as the backing store. This store does not persist anything across
 * application restarts.
 *
 * @param <K> Type of keys.
 * @param <V> Type of values.
 */
open class MemoryStoreCore<K, V>(
    private val merger: Merger<V>
) : StoreCore<K, V> {

    // All values in the store
    private val cache = ConcurrentHashMap<K, V>()

    // Flow of all updated values
    private val putStream = ConflatedBroadcastChannel<V>()

    // Flow of all deleted keys
    private val deleteStream = ConflatedBroadcastChannel<K>()

    // Listeners for given keys
    private val listeners = ConcurrentHashMap<K, ConflatedBroadcastChannel<V>>()

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
            getOrCreateChannel(key).asFlow()
        }
    }

    override suspend fun getAll(): List<V> {
        return cache.values.toList()
    }

    override fun getAllStream(): Flow<List<V>> {
        return putStream.asFlow()
            .combine(deleteStream.asFlow()) { _, _ -> Unit }
            .map { getAll() }
    }

    override suspend fun getKeys(): List<K> {
        return cache.keys().toList()
    }

    override fun getKeysStream(): Flow<List<K>> {
        return putStream.asFlow()
            .combine(deleteStream.asFlow()) { _, _ -> Unit }
            .map { getKeys() }
    }

    override suspend fun put(key: K, value: V): V? {
        lock.lock()

        Timber.w("PUT $key: $value")

        val (newValue, valuesDiffer) = mergeValues(cache[key], value, merger)

        if (!valuesDiffer) {
            // Data is already up to date
            lock.unlock()
            return null
        }

        cache[key] = newValue
        putStream.send(newValue)
        listeners[key]?.send(newValue)

        lock.unlock()
        return newValue
    }

    override suspend fun put(items: Map<K, V>): List<V> {
        return items.mapNotNull { (key, value) -> put(key, value) }
    }

    override fun getPutStream(): Flow<V> {
        return putStream.asFlow()
    }

    override suspend fun delete(key: K): Boolean {
        lock.lock()

        val deleted = cache.remove(key) != null
        if (deleted) deleteStream.send(key)

        lock.unlock()
        return deleted
    }

    override fun getDeleteStream(): Flow<K> {
        return deleteStream.asFlow()
    }

    private fun getOrCreateChannel(key: K): BroadcastChannel<V> {
        // Just return if channel already exists
        listeners[key]?.let {
            return@getOrCreateChannel it
        }

        // Doesn't exist, create new channel and init if value exist
        return ConflatedBroadcastChannel<V>().also { channel ->
            cache[key]?.let(channel::offer)
            listeners.putIfAbsent(key, channel)
        }
    }
}
