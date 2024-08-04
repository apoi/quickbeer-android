package quickbeer.android.domain.place.network

import org.threeten.bp.ZonedDateTime
import quickbeer.android.domain.place.Place
import quickbeer.android.util.JsonMapper
import quickbeer.android.util.ktx.normalize
import quickbeer.android.util.ktx.nullIfEmpty

object PlaceJsonMapper : JsonMapper<Int, Place, PlaceJson> {

    override fun map(key: Int, source: PlaceJson): Place {
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
            normalizedName = source.name.normalize().nullIfEmpty(),
            updated = ZonedDateTime.now(),
            accessed = null
        )
    }
}
