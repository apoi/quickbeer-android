package quickbeer.android.domain.beerlist.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.BeerSearchFetcher
import quickbeer.android.domain.beerlist.store.BeerSearchStore

class BeerSearchRepository(
    override val store: BeerSearchStore,
    fetcher: BeerSearchFetcher
) : ItemListRepository<String, Int, Beer, BeerJson>(store, fetcher) {

    /**
     * Merge exact search results from `getLocalStream` and results from a word
     * matching search that may find additional results from local database.
     */
    override fun getFuzzyLocalStream(key: String): Flow<List<Beer>> {
        return merge(store.getStream(key), store.search(key))
            .map { it.distinctBy(Beer::id) }
    }
}
