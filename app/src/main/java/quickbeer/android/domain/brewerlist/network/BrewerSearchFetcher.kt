package quickbeer.android.domain.brewerlist.network

import quickbeer.android.Constants.QUERY_MIN_LENGTH
import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.network.BrewerJson
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.exception.AppException.QueryTooShortException

class BrewerSearchFetcher(api: RateBeerApi) :
    Fetcher<String, List<Brewer>, List<BrewerJson>>(
        BrewerListJsonMapper,
        { query ->
            if (query.length >= QUERY_MIN_LENGTH) api.brewerSearch(query)
            else ApiResult.UnknownError(QueryTooShortException)
        }
    )
