package quickbeer.android.domain.beerlist.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.exception.AppException.QueryTooShortException
import quickbeer.android.util.ktx.normalize

class BeerSearchFetcher(api: RateBeerApi) :
    Fetcher<String, List<Beer>, List<BeerJson>>(
        BeerListJsonMapper,
        { query ->
            if (query.length > 3) api.beerSearch(query.normalize())
            else ApiResult.UnknownError(QueryTooShortException())
        }
    )
