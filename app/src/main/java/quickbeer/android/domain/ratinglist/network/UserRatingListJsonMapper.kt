package quickbeer.android.domain.ratinglist.network

import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.UserRatingJson
import quickbeer.android.domain.rating.network.UserRatingJsonMapper
import quickbeer.android.domain.user.User
import quickbeer.android.util.JsonMapper

class UserRatingListJsonMapper :
    JsonMapper<Pair<User, Int>, List<Pair<Beer, Rating>>, List<UserRatingJson>> {

    override fun map(key: Pair<User, Int>, source: List<UserRatingJson>): List<Pair<Beer, Rating>> {
        return source.map { UserRatingJsonMapper.map(key.first, it) }
    }
}
