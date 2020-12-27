package quickbeer.android.domain.beerlist.repository

import quickbeer.android.data.repository.repository.SingleItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.TopBeersFetcher
import quickbeer.android.domain.beerlist.store.TopBeersStore

class TopBeersRepository(
    store: TopBeersStore,
    fetcher: TopBeersFetcher
) : SingleItemListRepository<String, Int, Beer, BeerJson>(store, fetcher)
