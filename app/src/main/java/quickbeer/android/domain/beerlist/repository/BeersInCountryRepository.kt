package quickbeer.android.domain.beerlist.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.BeersInCountryFetcher
import quickbeer.android.domain.beerlist.store.BeersInCountryStore

class BeersInCountryRepository @Inject constructor(
    store: BeersInCountryStore,
    fetcher: BeersInCountryFetcher
) : ItemListRepository<String, Int, Beer, BeerJson>(store, fetcher)
