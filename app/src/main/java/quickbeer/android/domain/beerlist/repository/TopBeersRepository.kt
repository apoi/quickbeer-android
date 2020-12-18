package quickbeer.android.domain.beerlist.repository

import quickbeer.android.data.repository.repository.SingleItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.TopBeersFetcher
import quickbeer.android.domain.beerlist.store.TopBeersStore
import quickbeer.android.network.RateBeerApi

class TopBeersRepository(
    store: TopBeersStore,
    api: RateBeerApi
) : SingleItemListRepository<String, Int, Beer, BeerJson>(
    store, TopBeersFetcher(api)
)
