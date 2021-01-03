package quickbeer.android.domain.beer.store

import quickbeer.android.domain.beer.Beer
import quickbeer.android.util.Mapper

object BeerEntityMapper : Mapper<Beer, BeerEntity> {

    override fun mapFrom(source: Beer): BeerEntity {
        return BeerEntity(
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
            normalizedName = source.normalizedName,
            updated = source.updated,
            accessed = source.accessed
        )
    }

    override fun mapTo(source: BeerEntity): Beer {
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
            normalizedName = source.normalizedName,
            updated = source.updated,
            accessed = source.accessed
        )
    }
}
