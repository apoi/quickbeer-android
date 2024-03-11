package quickbeer.android.domain.rating.network

import quickbeer.android.domain.rating.Rating
import quickbeer.android.util.JsonMapper

object BeerRatingJsonMapper : JsonMapper<Int, Rating, BeerRatingJson> {

    override fun map(key: Int, source: BeerRatingJson): Rating {
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
