package quickbeer.android.domain.beerlist.network

import quickbeer.android.data.fetcher.SingleFetcher
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.network.RateBeerApi

class TopBeersFetcher(
    api: RateBeerApi
) : SingleFetcher<List<Beer>, List<BeerJson>>(
    BeerListJsonMapper(),
    { api.topBeers() }
)
