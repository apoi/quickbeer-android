package quickbeer.android.domain.place.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceJson(
    @field:Json(name = "PlaceID") val id: Int
)
