package quickbeer.android.domain.ratinglist.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.BeerRatingJson
import quickbeer.android.network.RateBeerApi

class BeerRatingFetcher(
    api: RateBeerApi
) : Fetcher<String, List<Rating>, List<BeerRatingJson>>(
    RatingListJsonMapper(),
    { beerId -> api.getRatings(beerId, 1) }
)

class BeerRatingPageFetcher(
    api: RateBeerApi
) : Fetcher<Pair<String, Int>, List<Rating>, List<BeerRatingJson>>(
    RatingListJsonMapper(),
    { (beerId, page) -> api.getRatings(beerId, page) }
)
