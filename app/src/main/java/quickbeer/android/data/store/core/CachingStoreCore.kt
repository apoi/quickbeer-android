package quickbeer.android.data.store.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import quickbeer.android.data.store.Merger
import quickbeer.android.data.store.StoreCore

/**
 * StoreCore that takes another core as parameter and adds a caching layer in front of it. This is
 * useful especially if the other core is slow, such as a Room backed core.
 */
open class CachingStoreCore<K, V>(
    private val persistingCore: StoreCore<K, V>,
    private val getKey: (V) -> K,
    merger: Merger<V> = StoreCore.takeNew()
) : StoreCore<K, V> {

    private val cacheCore = MemoryStoreCore<K, V>(merger)
    private val lock = Mutex(false)

    init {
        // Permanent subscription to all updates to keep cache up-to-date. This can't achieved with
        // put/delete methods as it's not given that a core isn't modified without going through
        // this cache.
        GlobalScope.launch(Dispatchers.IO) {
            persistingCore
                .getPutStream()
                .collect { value ->
                    lock.withLock { cacheCore.put(getKey(value), value) }
                }

            persistingCore
                .getDeleteStream()
                .collect { key ->
                    lock.withLock { cacheCore.delete(key) }
                }
        }
    }

    override suspend fun get(key: K): V? {
        val cached = cacheCore.get(key)
        if (cached != null) return cached

        return lock.withLock {
            persistingCore.get(key)?.also { value ->
                cacheCore.put(key, value)
            }
        }
    }

    override suspend fun get(keys: List<K>): List<V> {
        val cached = cacheCore.get(keys)
        if (keys.size == cached.size) return cached

        return lock.withLock {
            persistingCore.get(keys).also { values ->
                cacheCore.put(values.associateBy(getKey))
            }
        }
    }

    override suspend fun getKeys(): List<K> {
        return persistingCore.getKeys()
    }

    override fun getKeysStream(): Flow<List<K>> {
        return persistingCore.getKeysStream()
    }

    override fun getStream(key: K): Flow<V> {
        return persistingCore.getStream(key)
            .onEach { lock.withLock { cacheCore.put(key, it) } }
            .onStart { cacheCore.get(key)?.let { emit(it) } }
            .distinctUntilChanged()
    }

    override suspend fun getAll(): List<V> {
        return persistingCore.getAll()
    }

    override fun getAllStream(): Flow<List<V>> {
        return persistingCore.getAllStream()
    }

    override suspend fun put(key: K, value: V): V? {
        return persistingCore.put(key, value)
    }

    override suspend fun put(items: Map<K, V>): List<V> {
        return persistingCore.put(items)
    }

    override fun getPutStream(): Flow<V> {
        return persistingCore.getPutStream()
    }

    override suspend fun delete(key: K): Boolean {
        return persistingCore.delete(key)
    }

    override fun getDeleteStream(): Flow<K> {
        return persistingCore.getDeleteStream()
    }
}
