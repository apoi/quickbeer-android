package quickbeer.android.domain.user.network

import quickbeer.android.domain.user.User
import quickbeer.android.util.Mapper

object RateCountJsonMapper : Mapper<User, RateCountJson> {

    override fun mapFrom(source: User): RateCountJson {
        error("Not implemented")
    }

    override fun mapTo(source: RateCountJson): User {
        return User(
            id = null,
            username = null,
            rateCount = source.rateCount,
            tickCount = source.tickCount,
            placeCount = source.placeRatings
        )
    }
}
