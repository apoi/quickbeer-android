package quickbeer.android.domain.place.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.threeten.bp.ZonedDateTime

@JsonClass(generateAdapter = true)
data class PlaceJson(
    // Basics
    @field:Json(name = "PlaceID") val id: Int,
    @field:Json(name = "BrewerID") val brewerId: Int?,
    @field:Json(name = "PlaceType") val type: String?,
    @field:Json(name = "PlaceName") val name: String?,
    @field:Json(name = "PlainPlaceName") val plainName: String?,
    @field:Json(name = "Description") val description: String?,

    // Geographic information
    @field:Json(name = "MapID") val mapId: String?,
    @field:Json(name = "TourID") val tourId: Int?,
    @field:Json(name = "RegionID") val regionId: Int?,
    @field:Json(name = "MSA") val msa: String?,
    @field:Json(name = "Latitude") val latitude: Double?,
    @field:Json(name = "Longitude") val longitude: Double?,
    @field:Json(name = "GeoOverride") val geoOverride: Boolean?,

    // Address
    @field:Json(name = "Address") val address: String?,
    @field:Json(name = "City") val city: String?,
    @field:Json(name = "StateID") val stateId: Int?,
    @field:Json(name = "CountryID") val countryId: Int?,
    @field:Json(name = "PostalCode") val postalCode: String?,
    @field:Json(name = "PhoneNumber") val phoneNumber: String?,
    @field:Json(name = "PhoneCC") val phoneCountryCode: String?,
    @field:Json(name = "PhoneAC") val phoneAreaCode: String?,

    // Online information
    @field:Json(name = "WebSiteURL") val website: String?,
    @field:Json(name = "Facebook") val facebook: String?,
    @field:Json(name = "Twitter") val twitter: String?,
    @field:Json(name = "FoodMenu") val foodMenu: String?,
    @field:Json(name = "BeerMenu") val beerMenu: String?,

    // Ratings
    @field:Json(name = "Rating") val rating: String?,
    @field:Json(name = "AvgRating") val avgRating: Float?,
    @field:Json(name = "BayMean") val bayMean: Float?,
    @field:Json(name = "Percentile") val percentile: Int?,
    @field:Json(name = "RateCount") val rateCount: Int?,

    // Beer details
    @field:Json(name = "Taps") val taps: String?,
    @field:Json(name = "Bottles") val bottles: String?,
    @field:Json(name = "Seasonals") val seasonals: Boolean?,
    @field:Json(name = "RealAles") val realAles: Boolean?,
    @field:Json(name = "RareBeers") val rareBeers: Boolean?,
    @field:Json(name = "Macros") val macros: Boolean?,
    @field:Json(name = "Glassware") val glassware: Boolean?,
    @field:Json(name = "Singles") val singles: Boolean?,
    @field:Json(name = "Togo") val togo: Boolean?,

    // Other services
    @field:Json(name = "Wifi") val wifi: Boolean?,
    @field:Json(name = "Children") val children: Boolean?,
    @field:Json(name = "Reservations") val reservations: Boolean?,
    @field:Json(name = "Patio") val patio: Boolean?,
    @field:Json(name = "TV") val tv: Boolean?,
    @field:Json(name = "Games") val games: Boolean?,
    @field:Json(name = "Scene") val scene: String?,
    @field:Json(name = "Staff") val staff: String?,
    @field:Json(name = "Parking") val parking: String?,
    @field:Json(name = "Music") val music: String?,
    @field:Json(name = "Events") val events: String?,
    @field:Json(name = "Noise") val noise: String?,
    @field:Json(name = "Seating") val seating: String?,
    @field:Json(name = "Smoking") val smoking: Boolean?,
    @field:Json(name = "Cigars") val cigars: Boolean?,
    @field:Json(name = "Food") val food: String?,

    // Extras
    @field:Json(name = "Hours") val hours: String?,
    @field:Json(name = "Comments") val comments: String?,
    @field:Json(name = "Currency") val currency: String?,
    @field:Json(name = "MailOrder") val mailOrder: Boolean?,
    @field:Json(name = "MailOrderIntl") val mailOrderInternational: Boolean?,

    // Technical
    @field:Json(name = "Retired") val retired: Boolean?,
    @field:Json(name = "UserID") val userId: Int?,
    @field:Json(name = "EditedBy") val editedBy: Int?,
    @field:Json(name = "TimeAdded") val timeAdded: ZonedDateTime?,
    @field:Json(name = "TimeEdited") val timeEdited: ZonedDateTime?
)
