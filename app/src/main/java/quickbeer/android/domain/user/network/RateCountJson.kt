package quickbeer.android.domain.user.network

import com.squareup.moshi.Json
import org.threeten.bp.ZonedDateTime

data class RateCountJson(
    @field:Json(name = "ServerTime") val serverTime: ZonedDateTime?,
    @field:Json(name = "RateCount") val rateCount: Int?,
    @field:Json(name = "PlaceRatings") val placeRatings: Int?,
    @field:Json(name = "TickCount") val tickCount: Int?
)
