package quickbeer.android.domain.rating.network

import quickbeer.android.domain.rating.Rating
import quickbeer.android.util.JsonMapper

object RatingJsonMapper : JsonMapper<Int, Rating, RatingJson> {

    override fun map(key: Int, source: RatingJson): Rating {
        return Rating(
            id = source.id,
            beerId = null,
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
