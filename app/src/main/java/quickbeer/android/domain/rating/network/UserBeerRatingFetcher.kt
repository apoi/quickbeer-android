package quickbeer.android.domain.rating.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.user.User
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.first

class UserBeerRatingFetcher(
    api: RateBeerApi
) : Fetcher<Pair<User, Int>, Rating, BeerRatingJson>(
    BeerRatingJsonMapper,
    { (user, beerId) -> api.getRating(user.id, beerId).first() }
)
