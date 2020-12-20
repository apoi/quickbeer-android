package quickbeer.android.domain.beerlist.repository

import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.BeersInStyleFetcher
import quickbeer.android.domain.beerlist.store.BeersInStyleStore
import quickbeer.android.network.RateBeerApi

class BeersInStyleRepository(
    store: BeersInStyleStore,
    api: RateBeerApi
) : ItemListRepository<String, Int, Beer, BeerJson>(
    store, BeersInStyleFetcher(api)
)
