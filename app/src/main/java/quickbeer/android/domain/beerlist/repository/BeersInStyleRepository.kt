package quickbeer.android.domain.beerlist.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.BeersInStyleFetcher
import quickbeer.android.domain.beerlist.store.BeersInStyleStore

class BeersInStyleRepository @Inject constructor(
    store: BeersInStyleStore,
    fetcher: BeersInStyleFetcher
) : ItemListRepository<String, Int, Beer, BeerJson>(store, fetcher)
