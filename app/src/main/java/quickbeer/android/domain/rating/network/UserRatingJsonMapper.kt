package quickbeer.android.domain.rating.network

import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.user.User
import quickbeer.android.util.JsonMapper

object UserRatingJsonMapper : JsonMapper<User, Pair<Beer, Rating>, UserRatingJson> {

    override fun map(key: User, source: UserRatingJson): Pair<Beer, Rating> {
        val rating = Rating(
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
            userId = key.id,
            userName = key.username,
            city = null,
            stateId = null,
            state = null,
            countryId = null,
            country = null,
            rateCount = null,
            isDraft = false
        )

        val beer = Beer(
            id = source.beerId,
            name = source.beerName,
            brewerId = source.brewerId,
            brewerName = source.brewerName,
            contractBrewerId = null,
            contractBrewerName = null,
            averageRating = source.averageRating,
            overallRating = source.overallPercentile,
            styleRating = source.stylePercentile,
            rateCount = source.rateCount,
            countryId = null,
            styleId = source.beerStyleId,
            styleName = source.beerStyleName,
            alcohol = null,
            ibu = null,
            description = null,
            isAlias = null,
            isRetired = null,
            isVerified = null,
            unrateable = null,
            tickValue = null,
            tickDate = null,
            normalizedName = null,
            updated = null,
            accessed = null,
        )

        return Pair(beer, rating)
    }
}
