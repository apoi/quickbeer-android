package quickbeer.android.domain.brewer.network

import com.squareup.moshi.Json
import org.threeten.bp.ZonedDateTime

data class BrewerJson(
    @field:Json(name = "BrewerID") val id: Int,
    @field:Json(name = "BrewerName") val name: String?,
    @field:Json(name = "BrewerDescription") val description: String?,
    @field:Json(name = "BrewerAddress") val address: String?,
    @field:Json(name = "BrewerCity") val city: String?,
    @field:Json(name = "BrewerStateID") val stateId: Int?,
    @field:Json(name = "BrewerCountryID") val countryId: Int?,
    @field:Json(name = "BrewerZipCode") val zipCode: String?,
    @field:Json(name = "BrewerTypeID") val typeId: Int?,
    @field:Json(name = "BrewerType") val type: String?,
    @field:Json(name = "BrewerWebSite") val website: String?,
    @field:Json(name = "Facebook") val facebook: String?,
    @field:Json(name = "Twitter") val twitter: String?,
    @field:Json(name = "BrewerEmail") val email: String?,
    @field:Json(name = "BrewerPhone") val phone: String?,
    @field:Json(name = "Barrels") val barrels: Int?,
    @field:Json(name = "Opened") val founded: ZonedDateTime?,
    @field:Json(name = "EnteredOn") val enteredOn: ZonedDateTime?,
    @field:Json(name = "EnteredBy") val enteredBy: Int?,
    @field:Json(name = "LogoImage") val logo: String?,
    @field:Json(name = "ViewCount") val viewCount: String?,
    @field:Json(name = "Score") val score: Int?,
    @field:Json(name = "OOB") val outOfBusiness: Boolean?,
    @field:Json(name = "retired") val retired: Boolean?,
    @field:Json(name = "AreaCode") val areaCode: String?,
    @field:Json(name = "Hours") val hours: String?,
    @field:Json(name = "HeadBrewer") val headBrewer: String?,
    @field:Json(name = "MetroID") val metroId: String?,
    @field:Json(name = "MSA") val msa: String?,
    @field:Json(name = "RegionID") val regionId: String?
)
