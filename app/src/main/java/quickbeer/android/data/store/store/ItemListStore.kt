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
import timber.log.Timber

/**
 * Store for lists of items. Keeps index of the items in one core, and items themselves in another
 * core. This allows each item to exist in multiple indexes and be queried independently.
 *
 * @param <I> Type of index keys.
 * @param <K> Type of keys.
 * @param <V> Type of values.
 *
 * @param indexMapper Mapper class used when persisted indexes must be encoded
 * @param getKey Function for deriving value's key from value itself
 * @param indexCore Core storing mapping from index to ItemList
 * @param valueCore Core storing values matched by keys
 */
@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
open class ItemListStore<I, out K, V : Any>(
    private val indexMapper: IndexMapper<I>,
    private val getKey: (V) -> K,
    private val indexCore: StoreCore<I, ItemList<I, K>>,
    private val valueCore: StoreCore<K, V>,
) : Store<I, List<V>> {

    override suspend fun get(index: I): List<V> {
        Timber.w("GET " + indexMapper.encode(index))
        return withContext(Dispatchers.IO) {
            indexCore.get(indexMapper.encode(index))?.values
                ?.let { valueCore.get(it) }
                ?: emptyList()
        }
    }

    override fun getStream(index: I): Flow<List<V>> {
        Timber.w("GET STREAM " + indexMapper.encode(index))
        return indexCore.getStream(indexMapper.encode(index))
            .map { itemList -> itemList.values }
            .map(valueCore::get)
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getKeys(): List<I> {
        return indexCore.getKeys()
            .filter(indexMapper::matches)
            .map(indexMapper::decode)
    }

    override fun getKeysStream(): Flow<List<I>> {
        return indexCore.getKeysStream()
            .map {
                it.filter(indexMapper::matches)
                    .map(indexMapper::decode)
            }
    }

    open suspend fun getValueKeys(index: I): List<K> {
        return withContext(Dispatchers.IO) {
            indexCore.get(indexMapper.encode(index))?.values
                ?: emptyList()
        }
    }

    open fun getValueKeysStream(index: I): Flow<List<K>> {
        return indexCore.getStream(indexMapper.encode(index))
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

            val storeIndex = indexMapper.encode(index)
            val itemList = ItemList(storeIndex, values.map(getKey))
            val indexChanged = indexCore.put(storeIndex, itemList)

            newValues.isNotEmpty() || indexChanged != null
        }
    }

    /**
     * Deletes key from the index store. Does not delete any of the values,
     * as they may be referenced in other indexes.
     */
    override suspend fun delete(index: I): Boolean {
        return withContext(Dispatchers.IO) {
            indexCore.delete(indexMapper.encode(index))
        }
    }

    /**
     * Interface for mapping index to store persistence key. Useful when multiple stores use the
     * same core for storing indexes: this allows the stores to have unique persisted values.
     */
    interface IndexMapper<I> {

        /**
         * Encode index to store internal representation.
         */
        fun encode(index: I): I

        /**
         * Decode from store internal representation to index.
         */
        fun decode(value: I): I

        /**
         * Indicates if the value is valid index.
         */
        fun matches(value: I): Boolean
    }

    /**
     * Mapper returning the value itself.
     */
    class Identity<I> : IndexMapper<I> {
        override fun encode(index: I) = index
        override fun decode(value: I) = value
        override fun matches(value: I) = true
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

    private val store = ItemListStore(ItemListStore.Identity(), getKey, indexCore, valueCore)

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
