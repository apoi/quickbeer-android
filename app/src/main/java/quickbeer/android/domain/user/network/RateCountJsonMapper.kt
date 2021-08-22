package quickbeer.android.domain.user.network

import quickbeer.android.domain.user.User
import quickbeer.android.util.JsonMapper

object RateCountJsonMapper : JsonMapper<Int, User, RateCountJson> {

    override fun map(key: Int, source: RateCountJson): User {
        return User(
            id = key,
            username = null,
            rateCount = source.rateCount,
            tickCount = source.tickCount,
            placeCount = source.placeRatings
        )
    }
}
