package quickbeer.android.domain.brewerlist.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.network.BrewerJson
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.exception.AppException.QueryTooShortException
import quickbeer.android.util.ktx.normalize

class BrewerSearchFetcher(api: RateBeerApi) :
    Fetcher<String, List<Brewer>, List<BrewerJson>>(
        BrewerListJsonMapper,
        { query ->
            if (query.length > 3) api.brewerSearch(query.normalize())
            else ApiResult.UnknownError(QueryTooShortException())
        }
    )
