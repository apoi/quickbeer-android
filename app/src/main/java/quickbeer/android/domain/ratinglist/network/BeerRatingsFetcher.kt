package quickbeer.android.domain.ratinglist.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.RatingJson
import quickbeer.android.network.RateBeerApi

class BeerRatingsFetcher(
    api: RateBeerApi
) : Fetcher<String, List<Rating>, List<RatingJson>>(
    RatingListJsonMapper(),
    { beerId -> api.getRatings(beerId, 1) }
)

class BeerRatingsPageFetcher(
    api: RateBeerApi
) : Fetcher<Pair<String, Int>, List<Rating>, List<RatingJson>>(
    RatingListJsonMapper(),
    { (beerId, page) -> api.getRatings(beerId, page) }
)
