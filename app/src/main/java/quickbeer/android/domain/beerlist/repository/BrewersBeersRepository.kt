package quickbeer.android.domain.beerlist.repository

import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.BrewersBeersFetcher
import quickbeer.android.domain.beerlist.store.BeersInStyleStore

class BrewersBeersRepository(
    store: BeersInStyleStore,
    fetcher: BrewersBeersFetcher
) : ItemListRepository<String, Int, Beer, BeerJson>(store, fetcher)
