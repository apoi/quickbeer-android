package quickbeer.android.domain.user.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.threeten.bp.ZonedDateTime

@JsonClass(generateAdapter = true)
data class RateCountJson(
    @field:Json(name = "ServerTime") val serverTime: ZonedDateTime?,
    @field:Json(name = "RateCount") val rateCount: Int?,
    @field:Json(name = "PlaceRatings") val placeRatings: Int?,
    @field:Json(name = "TickCount") val tickCount: Int?
)
