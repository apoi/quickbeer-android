package quickbeer.android.data.repository.repository

import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.data.fetcher.SingleFetcher
import quickbeer.android.data.repository.Repository
import quickbeer.android.data.repository.SingleRepository
import quickbeer.android.data.store.store.ItemListStore
import quickbeer.android.data.store.store.SingleItemListStore
import quickbeer.android.network.result.ApiResult

/**
 * ItemListRepository handles lists of items.
 *
 * @param <I> Type of index keys.
 * @param <K> Type of keys.
 * @param <V> Type of values.
 * @param <E> Type of fetcher raw values.
 */
open class ItemListRepository<I : Any, out K : Any, V : Any, E : Any>(
    open val store: ItemListStore<I, K, V>,
    private val fetcher: Fetcher<I, List<V>, List<E>>
) : Repository<I, List<V>>() {

    override suspend fun persist(key: I, value: List<V>) {
        store.put(key, value)
    }

    override suspend fun getLocal(key: I): List<V>? {
        return store.get(key).let {
            if (it.isEmpty()) {
                null
            } else {
                it
            }
        }
    }

    override fun getLocalStream(key: I): Flow<List<V>> {
        return store.getStream(key)
    }

    override suspend fun fetchRemote(key: I): ApiResult<List<V>> {
        return fetcher.fetch(key)
    }
}

/**
 * Special case of ItemListRepository that only handles a single value.
 *
 * @param <I> Type of index keys.
 * @param <K> Type of keys.
 * @param <V> Type of values.
 * @param <E> Type of fetcher raw values.
 */
open class SingleItemListRepository<in I : Any, K : Any, V : Any, E : Any>(
    private val store: SingleItemListStore<I, K, V>,
    private val fetcher: SingleFetcher<List<V>, List<E>>
) : SingleRepository<List<V>>() {

    override suspend fun persist(value: List<V>) {
        store.put(value)
    }

    override suspend fun getLocal(): List<V>? {
        return store.get().let {
            if (it.isEmpty()) {
                null
            } else {
                it
            }
        }
    }

    override fun getLocalStream(): Flow<List<V>> {
        return store.getStream()
    }

    override suspend fun fetchRemote(): ApiResult<List<V>> {
        return fetcher.fetch()
    }
}
