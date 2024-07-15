package quickbeer.android.domain.feed.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.threeten.bp.ZonedDateTime

@JsonClass(generateAdapter = true)
data class FeedItemJson(
    @field:Json(name = "ActivityID") val activityId: Int,
    @field:Json(name = "UserID") val userId: Int?,
    @field:Json(name = "Username") val username: String?,
    @field:Json(name = "Type") val type: Int?,
    @field:Json(name = "LinkID") val linkId: Int?,
    @field:Json(name = "LinkText") val linkText: String?,
    @field:Json(name = "ActivityNumber") val activityNumber: Int?,
    @field:Json(name = "TimeEntered") val timeEntered: ZonedDateTime?,
    @field:Json(name = "NumComments") val numComments: Int?
)
