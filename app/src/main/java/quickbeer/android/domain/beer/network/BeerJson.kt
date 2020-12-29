package quickbeer.android.domain.beer.network

import com.squareup.moshi.Json
import org.threeten.bp.ZonedDateTime

data class BeerJson(
    @field:Json(name = "BeerID") val id: Int,
    @field:Json(name = "BeerName") val name: String?,
    @field:Json(name = "BrewerID") val brewerId: Int?,
    @field:Json(name = "BrewerName") val brewerName: String?,
    @field:Json(name = "ContractBrewerID") val contractBrewerId: Int?,
    @field:Json(name = "ContractBrewer") val contractBrewerName: String?,
    @field:Json(name = "AverageRating") val averageRating: Float?,
    @field:Json(name = "OverallPctl") val overallRating: Float?,
    @field:Json(name = "StylePctl") val styleRating: Float?,
    @field:Json(name = "RateCount") val rateCount: Int?,
    @field:Json(name = "BrewerCountryID") val countryId: Int?,
    @field:Json(name = "BeerStyleID") val styleId: Int?,
    @field:Json(name = "BeerStyleName") val styleName: String?,
    @field:Json(name = "Alcohol") val alcohol: Float?,
    @field:Json(name = "IBU") val ibu: Float?,
    @field:Json(name = "Description") val description: String?,
    @field:Json(name = "IsAlias") val isAlias: Boolean?,
    @field:Json(name = "Retired") val isRetired: Boolean?,
    @field:Json(name = "Verified") val isVerified: Boolean?,
    @field:Json(name = "Unrateable") val unrateable: Boolean?,
    @field:Json(name = "Liked") val tickValue: Int?,
    @field:Json(name = "TimeEntered") val tickDate: ZonedDateTime?
)
