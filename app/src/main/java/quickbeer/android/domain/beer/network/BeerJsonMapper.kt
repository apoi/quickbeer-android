package quickbeer.android.domain.beer.network

import quickbeer.android.domain.beer.Beer
import quickbeer.android.util.Mapper

object BeerJsonMapper : Mapper<Beer, BeerJson> {

    override fun mapFrom(source: Beer): BeerJson {
        return BeerJson(
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
            tickDate = source.tickDate
        )
    }

    override fun mapTo(source: BeerJson): Beer {
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
            tickDate = source.tickDate
        )
    }
}
