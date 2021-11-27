package quickbeer.android.domain.beer.network

import org.threeten.bp.ZonedDateTime
import quickbeer.android.domain.beer.Beer
import quickbeer.android.util.JsonMapper
import quickbeer.android.util.ktx.normalize
import quickbeer.android.util.ktx.nullIfEmpty

object BeerJsonMapper : JsonMapper<Int, Beer, BeerJson> {

    override fun map(key: Int, source: BeerJson): Beer {
        return Beer(
            id = source.id,
            name = source.name,
            brewerId = source.brewerId,
            brewerName = source.brewerName,
            contractBrewerId = source.contractBrewerId,
            contractBrewerName = source.contractBrewerName,
            averageRating = source.averageRating,
            overallRating = source.overallRating,
            styleRating = source.styleRating,
            rateCount = source.rateCount,
            countryId = source.countryId,
            styleId = source.styleId,
            styleName = source.styleName,
            alcohol = source.alcohol,
            ibu = source.ibu,
            description = source.description,
            isAlias = source.isAlias,
            isRetired = source.isRetired,
            isVerified = source.isVerified,
            unrateable = source.unrateable,
            tickValue = source.tickValue,
            tickDate = source.tickDate,
            normalizedName = source.name.normalize().nullIfEmpty(),
            updated = ZonedDateTime.now(),
            accessed = null
        )
    }
}
