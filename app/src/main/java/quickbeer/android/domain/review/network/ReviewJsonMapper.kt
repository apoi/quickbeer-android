package quickbeer.android.domain.review.network

import quickbeer.android.domain.review.Review
import quickbeer.android.util.JsonMapper

object ReviewJsonMapper : JsonMapper<Int, Review, ReviewJson> {

    override fun map(key: Int, source: ReviewJson): Review {
        return Review(
            id = source.id,
            appearance = source.appearance,
            aroma = source.aroma,
            flavor = source.flavor,
            mouthfeel = source.mouthfeel,
            overall = source.overall,
            totalScore = source.totalScore,
            comments = source.comments,
            timeEntered = source.timeEntered,
            timeUpdated = source.timeUpdated,
            userId = source.userId,
            userName = source.userName,
            city = source.city,
            stateId = source.stateId,
            state = source.state,
            countryId = source.countryId,
            country = source.country,
            rateCount = source.rateCount
        )
    }
}
