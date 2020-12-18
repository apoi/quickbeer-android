package quickbeer.android.domain.style.network

import com.squareup.moshi.Json

data class StyleJson(
    @field:Json(name = "BeerStyleID") val id: Int,
    @field:Json(name = "BeerStyleName") val name: String,
    @field:Json(name = "BeerStyleDescription") val description: String?,
    @field:Json(name = "BeerStyleParent") val parent: Int?,
    @field:Json(name = "BeerStyleCategory") val category: Int?,
)
