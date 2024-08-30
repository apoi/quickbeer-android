package quickbeer.android.domain.place

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.ZonedDateTime
import quickbeer.android.Constants
import quickbeer.android.data.store.Merger
import quickbeer.android.util.ktx.orLater

@Parcelize
data class Place(
    val id: Int,
    val brewerId: Int?,
    val type: Type?,
    val name: String?,
    val plainName: String?,
    val description: String?,
    val mapId: String?,
    val tourId: Int?,
    val regionId: Int?,
    val msa: String?,
    val latitude: Double?,
    val longitude: Double?,
    val geoOverride: Boolean?,
    val address: String?,
    val city: String?,
    val stateId: Int?,
    val countryId: Int?,
    val postalCode: String?,
    val phoneNumber: String?,
    val phoneCountryCode: String?,
    val phoneAreaCode: String?,
    val website: String?,
    val facebook: String?,
    val twitter: String?,
    val foodMenu: String?,
    val beerMenu: String?,
    val rating: String?,
    val avgRating: Float?,
    val bayMean: Float?,
    val percentile: Int?,
    val rateCount: Int?,
    val taps: String?,
    val bottles: String?,
    val seasonals: Boolean?,
    val realAles: Boolean?,
    val rareBeers: Boolean?,
    val macros: Boolean?,
    val glassware: Boolean?,
    val singles: Boolean?,
    val togo: Boolean?,
    val wifi: Boolean?,
    val children: Boolean?,
    val reservations: Boolean?,
    val patio: Boolean?,
    val tv: Boolean?,
    val games: Boolean?,
    val scene: String?,
    val staff: String?,
    val parking: String?,
    val music: String?,
    val events: String?,
    val noise: String?,
    val seating: String?,
    val smoking: Boolean?,
    val cigars: Boolean?,
    val food: String?,
    val hours: String?,
    val comments: String?,
    val currency: String?,
    val mailOrder: Boolean?,
    val mailOrderInternational: Boolean?,
    val retired: Boolean?,
    val userId: Int?,
    val editedBy: Int?,
    val timeAdded: ZonedDateTime?,
    val timeEdited: ZonedDateTime?,
    // Application fields
    val normalizedName: String?,
    val updated: ZonedDateTime?,
    val accessed: ZonedDateTime?
) : Parcelable {

    fun imageUri(): String {
        return Constants.PLACE_IMAGE_PATH.format(id)
    }

    enum class Type(val value: Int) {
        UNKNOWN(0),
        BREWPUB(1),
        BAR(2),
        BEER_STORE(3),
        RESTAURANT(4),
        BREWERY(5),
        HOME_BREWS_HOP(6),
        GROCERY_STORE(7),
        INTERNET_BASED(8);

        companion object {
            fun fromValue(value: Int?): Type {
                return entries.find { it.value == value } ?: UNKNOWN
            }
        }
    }

    companion object {
        val merger: Merger<Place> = { old, new ->
            Place(
                id = new.id,
                brewerId = new.brewerId ?: old.brewerId,
                type = new.type ?: old.type,
                name = new.name ?: old.name,
                plainName = new.plainName ?: old.plainName,
                description = new.description ?: old.description,
                mapId = new.mapId ?: old.mapId,
                tourId = new.tourId ?: old.tourId,
                regionId = new.regionId ?: old.regionId,
                msa = new.msa ?: old.msa,
                latitude = new.latitude ?: old.latitude,
                longitude = new.longitude ?: old.longitude,
                geoOverride = new.geoOverride ?: old.geoOverride,
                address = new.address ?: old.address,
                city = new.city ?: old.city,
                stateId = new.stateId ?: old.stateId,
                countryId = new.countryId ?: old.countryId,
                postalCode = new.postalCode ?: old.postalCode,
                phoneNumber = new.phoneNumber ?: old.phoneNumber,
                phoneCountryCode = new.phoneCountryCode ?: old.phoneCountryCode,
                phoneAreaCode = new.phoneAreaCode ?: old.phoneAreaCode,
                website = new.website ?: old.website,
                facebook = new.facebook ?: old.facebook,
                twitter = new.twitter ?: old.twitter,
                foodMenu = new.foodMenu ?: old.foodMenu,
                beerMenu = new.beerMenu ?: old.beerMenu,
                rating = new.rating ?: old.rating,
                avgRating = new.avgRating ?: old.avgRating,
                bayMean = new.bayMean ?: old.bayMean,
                percentile = new.percentile ?: old.percentile,
                rateCount = new.rateCount ?: old.rateCount,
                taps = new.taps ?: old.taps,
                bottles = new.bottles ?: old.bottles,
                seasonals = new.seasonals ?: old.seasonals,
                realAles = new.realAles ?: old.realAles,
                rareBeers = new.rareBeers ?: old.rareBeers,
                macros = new.macros ?: old.macros,
                glassware = new.glassware ?: old.glassware,
                singles = new.singles ?: old.singles,
                togo = new.togo ?: old.togo,
                wifi = new.wifi ?: old.wifi,
                children = new.children ?: old.children,
                reservations = new.reservations ?: old.reservations,
                patio = new.patio ?: old.patio,
                tv = new.tv ?: old.tv,
                games = new.games ?: old.games,
                scene = new.scene ?: old.scene,
                staff = new.staff ?: old.staff,
                parking = new.parking ?: old.parking,
                music = new.music ?: old.music,
                events = new.events ?: old.events,
                noise = new.noise ?: old.noise,
                seating = new.seating ?: old.seating,
                smoking = new.smoking ?: old.smoking,
                cigars = new.cigars ?: old.cigars,
                food = new.food ?: old.food,
                hours = new.hours ?: old.hours,
                comments = new.comments ?: old.comments,
                currency = new.currency ?: old.currency,
                mailOrder = new.mailOrder ?: old.mailOrder,
                mailOrderInternational = new.mailOrderInternational ?: old.mailOrderInternational,
                retired = new.retired ?: old.retired,
                userId = new.userId ?: old.userId,
                editedBy = new.editedBy ?: old.editedBy,
                timeAdded = new.timeAdded ?: old.timeAdded,
                timeEdited = new.timeEdited ?: old.timeEdited,
                normalizedName = new.normalizedName ?: old.normalizedName,
                updated = new.updated.orLater(old.updated),
                accessed = new.accessed.orLater(old.accessed)
            )
        }
    }
}
