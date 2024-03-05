package quickbeer.android.domain.ratinglist.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.UsersRatingJson
import quickbeer.android.domain.user.User
import quickbeer.android.network.RateBeerApi

class UsersRatingsPageFetcher(
    api: RateBeerApi
) : Fetcher<Pair<User, Int>, List<Rating>, List<UsersRatingJson>>(
    UsersRatingListJsonMapper(),
    { (_, page: Int) -> api.getUsersRatings(page) }
)
