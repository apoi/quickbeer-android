package quickbeer.android.domain.beerlist.repository

import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.BeerSearchFetcher
import quickbeer.android.domain.beerlist.store.BeerSearchStore

class BeerSearchRepository(
    override val store: BeerSearchStore,
    fetcher: BeerSearchFetcher
) : ItemListRepository<String, Int, Beer, BeerJson>(store, fetcher) {

    override fun getFuzzyLocalStream(key: String): Flow<List<Beer>> {
        return store.search(key)
    }
}
