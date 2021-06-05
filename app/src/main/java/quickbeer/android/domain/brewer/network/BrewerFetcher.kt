package quickbeer.android.domain.brewer.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.first

class BrewerFetcher(
    api: RateBeerApi
) : Fetcher<Int, Brewer, BrewerJson>(BrewerJsonMapper, { key -> api.brewer(key).first() })
