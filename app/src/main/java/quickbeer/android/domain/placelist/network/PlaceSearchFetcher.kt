package quickbeer.android.domain.placelist.network

import quickbeer.android.Constants.QUERY_MIN_LENGTH
import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.place.Place
import quickbeer.android.domain.place.network.PlaceJson
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.exception.AppException.QueryTooShortException

class PlaceSearchFetcher(
    api: RateBeerApi
) : Fetcher<String, List<Place>, List<PlaceJson>>(
    PlaceListJsonMapper,
    { query ->
        if (query.length >= QUERY_MIN_LENGTH) {
            api.placeSearch(query)
        } else {
            ApiResult.UnknownError(QueryTooShortException)
        }
    }
)
