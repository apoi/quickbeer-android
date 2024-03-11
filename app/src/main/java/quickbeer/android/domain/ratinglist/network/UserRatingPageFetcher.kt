package quickbeer.android.domain.ratinglist.network

import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.login.LoginAndRetryFetcher
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.UserRatingJson
import quickbeer.android.domain.user.User
import quickbeer.android.network.RateBeerApi

class UserRatingPageFetcher(
    api: RateBeerApi,
    loginManager: LoginManager
) : LoginAndRetryFetcher<Pair<User, Int>, List<Pair<Beer, Rating>>, List<UserRatingJson>>(
    UserRatingListJsonMapper(),
    { (_, page: Int) -> api.getUsersRatings(page) },
    loginManager
)
