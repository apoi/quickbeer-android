package quickbeer.android.domain.place.store

import quickbeer.android.domain.place.Place
import quickbeer.android.util.Mapper

object PlaceEntityMapper : Mapper<Place, PlaceEntity> {

    override fun mapFrom(source: Place): PlaceEntity {
        return PlaceEntity(
            id = source.id,
            name = source.name,
            type = source.type?.value,
            brewerId = source.brewerId,
            userId = source.userId,
            isRetired = source.isRetired,
            address = source.address,
            city = source.city,
            postalCode = source.postalCode,
            countryId = source.countryId,
            stateId = source.stateId,
            hours = source.hours,
            taps = source.taps,
            bottles = source.bottles,
            website = source.website,
            facebook = source.facebook,
            twitter = source.twitter,
            phoneNumber = source.phoneNumber,
            latitude = source.latitude,
            longitude = source.longitude,
            realRating = source.realRating,
            weightedRating = source.weightedRating,
            overallPercentile = source.overallPercentile,
            rateCount = source.rateCount,
            normalizedName = source.normalizedName,
            updated = source.updated,
            accessed = source.accessed
        )
    }

    override fun mapTo(source: PlaceEntity): Place {
        return Place(
            id = source.id,
            name = source.name,
            type = Place.Type.fromValue(source.type),
            brewerId = source.brewerId,
            userId = source.userId,
            isRetired = source.isRetired,
            address = source.address,
            city = source.city,
            postalCode = source.postalCode,
            countryId = source.countryId,
            stateId = source.stateId,
            hours = source.hours,
            taps = source.taps,
            bottles = source.bottles,
            website = source.website,
            facebook = source.facebook,
            twitter = source.twitter,
            phoneNumber = source.phoneNumber,
            latitude = source.latitude,
            longitude = source.longitude,
            realRating = source.realRating,
            weightedRating = source.weightedRating,
            overallPercentile = source.overallPercentile,
            rateCount = source.rateCount,
            normalizedName = source.normalizedName,
            updated = source.updated,
            accessed = source.accessed
        )
    }
}
