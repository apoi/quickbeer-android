package quickbeer.android.domain.beersearch.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.network.RateBeerApi

class BeerSearchFetcher(api: RateBeerApi) :
    Fetcher<String, List<Beer>, List<BeerJson>>(
        BeerListJsonMapper, { query -> api.search(query) }
    )
