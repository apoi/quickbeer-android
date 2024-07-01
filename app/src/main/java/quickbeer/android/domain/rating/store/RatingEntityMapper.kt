package quickbeer.android.domain.rating.store

import quickbeer.android.domain.rating.Rating
import quickbeer.android.util.Mapper

object RatingEntityMapper : Mapper<Rating, RatingEntity> {

    override fun mapFrom(source: Rating): RatingEntity {
        return RatingEntity(
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
            userId = source.userId,
            userName = source.userName,
            city = source.city,
            stateId = source.stateId,
            state = source.state,
            countryId = source.countryId,
            country = source.country,
            rateCount = source.rateCount,
            isDraft = source.isDraft
        )
    }

    override fun mapTo(source: RatingEntity): Rating {
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
            userId = source.userId,
            userName = source.userName,
            city = source.city,
            stateId = source.stateId,
            state = source.state,
            countryId = source.countryId,
            country = source.country,
            rateCount = source.rateCount,
            isDraft = source.isDraft
        )
    }
}
