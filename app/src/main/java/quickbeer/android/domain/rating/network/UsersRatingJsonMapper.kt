package quickbeer.android.domain.rating.network

import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.user.User
import quickbeer.android.util.JsonMapper

object UsersRatingJsonMapper : JsonMapper<User, Rating, UsersRatingJson> {

    override fun map(key: User, source: UsersRatingJson): Rating {
        return Rating(
            id = source.id,
            beerId = source.beerId,
            appearance = source.appearance,
            aroma = source.aroma,
            flavor = source.flavor,
            mouthfeel = source.mouthfeel,
            overall = source.overall,
            totalScore = source.totalScore,
            comments = source.comments,
            timeEntered = source.timeEntered,
            timeUpdated = source.timeUpdated,

            // User's own ratings don't contain the user details
            userId = key.id,
            userName = null,
            city = null,
            stateId = null,
            state = null,
            countryId = null,
            country = null,
            rateCount = null
        )
    }
}
