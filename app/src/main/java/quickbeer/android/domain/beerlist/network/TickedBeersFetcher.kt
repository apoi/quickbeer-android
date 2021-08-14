package quickbeer.android.domain.beerlist.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.network.RateBeerApi

class TickedBeersFetcher(
    api: RateBeerApi
) : Fetcher<String, List<Beer>, List<BeerJson>>(
    BeerListJsonMapper, { userId -> api.getTicks(userId) }
)
