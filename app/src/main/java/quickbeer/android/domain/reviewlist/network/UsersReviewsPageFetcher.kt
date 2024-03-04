package quickbeer.android.domain.reviewlist.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.network.UsersReviewJson
import quickbeer.android.domain.user.User
import quickbeer.android.network.RateBeerApi

class UsersReviewsPageFetcher(
    api: RateBeerApi
) : Fetcher<Pair<User, Int>, List<Review>, List<UsersReviewJson>>(
    UsersReviewListJsonMapper(),
    { (_, page: Int) -> api.getUsersReviews(page) }
)