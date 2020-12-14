package quickbeer.android.data.store.store

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import quickbeer.android.data.repository.repository.ItemList
import quickbeer.android.data.store.SingleStore
import quickbeer.android.data.store.Store
import quickbeer.android.data.store.StoreCore

/**
 * Store for lists of items. Keeps index of the items in one core, and items themselves in another
 * core. This allows each item to exist in multiple indexes and be queried independently.
 *
 * @param <I> Type of index keys.
 * @param <K> Type of keys.
 * @param <V> Type of values.
 */
@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
open class ItemListStore<I, out K, V : Any>(
    private val getKey: (V) -> K,
    private val indexCore: StoreCore<I, ItemList<I, K>>,
    private val valueCore: StoreCore<K, V>
) : Store<I, List<V>> {

    override suspend fun get(index: I): List<V> {
        return withContext(Dispatchers.IO) {
            indexCore.get(index)?.values
                ?.let { valueCore.get(it) }
                ?: emptyList()
        }
    }

    override fun getStream(index: I): Flow<List<V>> {
        return indexCore.getStream(index)
            .map { index -> index.values }
            .map(valueCore::get)
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getKeys(): List<I> {
        return indexCore.getKeys()
    }

    override fun getKeysStream(): Flow<List<I>> {
        return indexCore.getKeysStream()
    }

    suspend fun getValueKeys(index: I): List<K> {
        return withContext(Dispatchers.IO) {
            indexCore.get(index)?.values
                ?: emptyList()
        }
    }

    fun getValueKeysStream(index: I): Flow<List<K>> {
        return indexCore.getStream(index)
            .map { it.values }
            .flowOn(Dispatchers.IO)
    }

    /**
     * Put all values to store, and add index to keep track of all the values.
     */
    override suspend fun put(index: I, values: List<V>): Boolean {
        return withContext(Dispatchers.IO) {
            // Put values first in case there's a listener for the index. This way values
            // already exist for any listeners to query.
            val newValues = values
                .map { value -> Pair(getKey(value), value) }
                .let { items -> valueCore.put(items.toMap()) }

            val indexChanged = indexCore.put(index, ItemList(index, values.map(getKey)))

            newValues.isNotEmpty() || indexChanged != null
        }
    }

    /**
     * Deletes key from the index store. Does not delete any of the values,
     * as they may be referenced in other indexes.
     */
    override suspend fun delete(index: I): Boolean {
        return withContext(Dispatchers.IO) {
            indexCore.delete(index)
        }
    }
}

/**
 * Special case of ItemListStore that only contains a single value.
 *
 * @param <I> Type of index keys.
 * @param <K> Type of keys.
 * @param <V> Type of values.
 */
open class SingleItemListStore<I, out K, V : Any>(
    private val indexKey: I,
    getKey: (V) -> K,
    indexCore: StoreCore<I, ItemList<I, K>>,
    valueCore: StoreCore<K, V>
) : SingleStore<List<V>> {

    private val store = ItemListStore(getKey, indexCore, valueCore)

    override suspend fun get(): List<V> {
        return store.get(indexKey)
    }

    override fun getStream(): Flow<List<V>> {
        return store.getStream(indexKey)
    }

    suspend fun getValueKeys(): List<K> {
        return store.getValueKeys(indexKey)
    }

    fun getValueKeysStream(): Flow<List<K>> {
        return store.getValueKeysStream(indexKey)
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun put(values: List<V>): Boolean {
        return store.put(indexKey, values)
    }

    override suspend fun delete(): Boolean {
        return store.delete(indexKey)
    }
}
