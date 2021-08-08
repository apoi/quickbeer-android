package quickbeer.android.domain.beerlist.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.SingleItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.TickedBeersFetcher
import quickbeer.android.domain.beerlist.store.TickedBeersStore

class TickedBeersRepository @Inject constructor(
    store: TickedBeersStore,
    fetcher: TickedBeersFetcher
) : SingleItemListRepository<String, Int, Beer, BeerJson>(store, fetcher)
