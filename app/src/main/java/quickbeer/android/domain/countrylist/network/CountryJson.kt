package quickbeer.android.domain.countrylist.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountryJson(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "code") val code: String,
    @field:Json(name = "official") val official: String,
    @field:Json(name = "refer") val refer: String,
    @field:Json(name = "capital") val capital: String,
    @field:Json(name = "region") val region: String,
    @field:Json(name = "subregion") val subregion: String,
    @field:Json(name = "wikipedia") val wikipedia: String,
    @field:Json(name = "wikipedia_beer") val wikipediaBeer: String?,
    @field:Json(name = "description") val description: String?
)
