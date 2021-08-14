package quickbeer.android.domain.user.network

import com.squareup.moshi.Json

data class RateCountJson(
    @field:Json(name = "ServerTime") val serverTime: Int?,
    @field:Json(name = "RateCount") val rateCount: Int?,
    @field:Json(name = "PlaceRatings") val placeRatings: Int?,
    @field:Json(name = "TickCount") val tickCount: Int?
)
