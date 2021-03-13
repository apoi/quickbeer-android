package quickbeer.android.domain.reviewlist.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.network.ReviewJson
import quickbeer.android.domain.reviewlist.ReviewPage
import quickbeer.android.network.RateBeerApi

class ReviewsForBeerFetcher(api: RateBeerApi) :
    Fetcher<ReviewPage, List<Review>, List<ReviewJson>>(
        ReviewListJsonMapper, { (beerId, page) -> api.getReviews(beerId, page) }
    )