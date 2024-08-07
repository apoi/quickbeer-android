package quickbeer.android.domain.beerlist.network

import quickbeer.android.Constants.QUERY_MIN_LENGTH
import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.exception.AppException.QueryTooShortException

class BarcodeSearchFetcher(
    api: RateBeerApi
) : Fetcher<String, List<Beer>, List<BeerJson>>(
    BeerListJsonMapper(),
    { query ->
        if (query.length >= QUERY_MIN_LENGTH) {
            api.barcodeSearch(query)
        } else {
            ApiResult.UnknownError(QueryTooShortException)
        }
    }
)
