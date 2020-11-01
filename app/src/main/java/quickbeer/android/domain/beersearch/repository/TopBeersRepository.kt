package quickbeer.android.domain.beersearch.repository

import quickbeer.android.data.repository.repository.SingleItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beersearch.network.BeerSearchFetcher
import quickbeer.android.domain.beersearch.network.TopBeersFetcher
import quickbeer.android.domain.beersearch.store.BeerListStore
import quickbeer.android.domain.beersearch.store.SingleBeerListStore
import quickbeer.android.domain.beersearch.store.TopBeersStore
import quickbeer.android.network.RateBeerApi

class TopBeersRepository(
    store: TopBeersStore,
    api: RateBeerApi
) : SingleItemListRepository<String, Int, Beer, BeerJson>(
    store, TopBeersFetcher(api)
)
