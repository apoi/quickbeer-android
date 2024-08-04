package quickbeer.android.domain.place

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.store.Merger
import quickbeer.android.util.ktx.orLater

@Parcelize
data class Place(
    val id: Int,
    val name: String?,
    val type: Type?,
    val brewerId: Long?,
    val userId: Long?,
    val isRetired: Boolean?,
    val address: String?,
    val city: String?,
    val postalCode: String?,
    val countryId: Int?,
    val stateId: Int?,
    val hours: String?,
    val taps: String?,
    val bottles: String?,
    val website: String?,
    val facebook: String?,
    val twitter: String?,
    val phoneNumber: String?,
    val latitude: Float?,
    val longitude: Float?,
    val realRating: Float?,
    val weightedRating: Float?,
    val overallPercentile: Float?,
    val rateCount: Int?,
    // Application fields
    val normalizedName: String?,
    val updated: ZonedDateTime?,
    val accessed: ZonedDateTime?
) : Parcelable {

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
                name = new.name ?: old.name,
                type = new.type ?: old.type,
                brewerId = new.brewerId ?: old.brewerId,
                userId = new.userId ?: old.userId,
                isRetired = new.isRetired ?: old.isRetired,
                address = new.address ?: old.address,
                city = new.city ?: old.city,
                postalCode = new.postalCode ?: old.postalCode,
                countryId = new.countryId ?: old.countryId,
                stateId = new.stateId ?: old.stateId,
                hours = new.hours ?: old.hours,
                taps = new.taps ?: old.taps,
                bottles = new.bottles ?: old.bottles,
                website = new.website ?: old.website,
                facebook = new.facebook ?: old.facebook,
                twitter = new.twitter ?: old.twitter,
                phoneNumber = new.phoneNumber ?: old.phoneNumber,
                latitude = new.latitude ?: old.latitude,
                longitude = new.longitude ?: old.longitude,
                realRating = new.realRating ?: old.realRating,
                weightedRating = new.weightedRating ?: old.weightedRating,
                overallPercentile = new.overallPercentile ?: old.overallPercentile,
                rateCount = new.rateCount ?: old.rateCount,
                normalizedName = new.normalizedName ?: old.normalizedName,
                updated = new.updated.orLater(old.updated),
                accessed = new.accessed.orLater(old.accessed)
            )
        }
    }
}
