package quickbeer.android.domain.user.network

import org.threeten.bp.ZonedDateTime
import quickbeer.android.domain.user.User
import quickbeer.android.util.JsonMapper

object RateCountJsonMapper : JsonMapper<Int, User, RateCountJson> {

    override fun map(key: Int, source: RateCountJson): User {
        return User(
            id = key,
            username = null,
            password = null,
            loggedIn = null,
            countryId = null,
            rateCount = source.rateCount,
            tickCount = source.tickCount,
            placeCount = source.placeRatings,
            updated = ZonedDateTime.now(),
        )
    }
}
