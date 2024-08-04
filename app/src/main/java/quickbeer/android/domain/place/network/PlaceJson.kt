package quickbeer.android.domain.place.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceJson(
    @field:Json(name = "PlaceID") val id: Int,
    @field:Json(name = "Name") val name: String?,
    @field:Json(name = "Type") val type: Int?,
    @field:Json(name = "BrewerID") val brewerId: Long?,
    @field:Json(name = "UserID") val userId: Long?,
    @field:Json(name = "IsRetired") val isRetired: Boolean?,
    @field:Json(name = "Address") val address: String?,
    @field:Json(name = "City") val city: String?,
    @field:Json(name = "PostalCode") val postalCode: String?,
    @field:Json(name = "CountryID") val countryId: Int?,
    @field:Json(name = "StateID") val stateId: Int?,
    @field:Json(name = "Hours") val hours: String?,
    @field:Json(name = "Taps") val taps: String?,
    @field:Json(name = "Bottles") val bottles: String?,
    @field:Json(name = "Website") val website: String?,
    @field:Json(name = "Facebook") val facebook: String?,
    @field:Json(name = "Twitter") val twitter: String?,
    @field:Json(name = "PhoneNumber") val phoneNumber: String?,
    @field:Json(name = "Latitude") val latitude: Float?,
    @field:Json(name = "Longitude") val longitude: Float?,
    @field:Json(name = "RealRating") val realRating: Float?,
    @field:Json(name = "WeightedRating") val weightedRating: Float?,
    @field:Json(name = "OverallPercentile") val overallPercentile: Float?,
    @field:Json(name = "RateCount") val rateCount: Int?
)
