package quickbeer.android.domain.ratinglist.network

import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.UsersRatingJson
import quickbeer.android.domain.rating.network.UsersRatingJsonMapper
import quickbeer.android.domain.user.User
import quickbeer.android.util.JsonMapper

class UsersRatingListJsonMapper : JsonMapper<Pair<User, Int>, List<Rating>, List<UsersRatingJson>> {

    override fun map(key: Pair<User, Int>, source: List<UsersRatingJson>): List<Rating> {
        return source.map { UsersRatingJsonMapper.map(key.first, it) }
    }
}
