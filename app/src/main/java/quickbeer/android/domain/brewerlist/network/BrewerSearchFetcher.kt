package quickbeer.android.domain.brewerlist.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.network.BrewerJson
import quickbeer.android.network.RateBeerApi

class BrewerSearchFetcher(api: RateBeerApi) :
    Fetcher<String, List<Brewer>, List<BrewerJson>>(
        BrewerListJsonMapper, { query -> api.brewerSearch(query) }
    )
