package quickbeer.android.domain.reviewlist.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.network.ReviewJson
import quickbeer.android.network.RateBeerApi

class BeerReviewsFetcher(
    api: RateBeerApi
) : Fetcher<String, List<Review>, List<ReviewJson>>(
    ReviewListJsonMapper, { beerId -> api.getReviews(beerId, 1) }
)

class BeerReviewsPageFetcher(
    api: RateBeerApi
) : Fetcher<Pair<String, Int>, List<Review>, List<ReviewJson>>(
    ReviewListJsonMapper, { (beerId, page) -> api.getReviews(beerId, page) }
)
