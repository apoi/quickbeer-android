package quickbeer.android.domain.beerlist.repository

import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.BeerSearchFetcher
import quickbeer.android.domain.beerlist.store.BeerSearchStore
import quickbeer.android.network.RateBeerApi

class BeerSearchRepository(
    store: BeerSearchStore,
    api: RateBeerApi
) : ItemListRepository<String, Int, Beer, BeerJson>(
    store, BeerSearchFetcher(api)
)
