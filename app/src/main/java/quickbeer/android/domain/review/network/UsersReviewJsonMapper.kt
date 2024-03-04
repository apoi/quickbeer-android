package quickbeer.android.domain.review.network

import quickbeer.android.domain.review.Review
import quickbeer.android.domain.user.User
import quickbeer.android.util.JsonMapper

object UsersReviewJsonMapper : JsonMapper<User, Review, UsersReviewJson> {

    override fun map(key: User, source: UsersReviewJson): Review {
        return Review(
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

            // User's own reviews don't contain the user details
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
