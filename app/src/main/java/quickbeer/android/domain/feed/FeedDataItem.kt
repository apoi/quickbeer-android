package quickbeer.android.domain.feed

import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.store.Merger
import quickbeer.android.util.ktx.orLater
import timber.log.Timber

data class FeedDataItem(
    val id: Int,
    val userId: Int?,
    val username: String?,
    val type: Type?,
    val linkId: Int?,
    val linkText: String?,
    val activityNumber: Int?,
    val timeEntered: ZonedDateTime?,
    val numComments: Int?
) {

    fun beerId(): Int? {
        return when (type) {
            Type.BEER_ADDED, Type.BEER_RATING -> linkId?.takeIf { it > 0 }
            else -> null
        }
    }

    fun toFeedItem(): FeedItem? {
        return when (type) {
            Type.BEER_RATING -> {
                FeedItem.BeerRated(
                    id = id,
                    userId = userId ?: error("Id required"),
                    userName = username ?: error("Name required"),
                    beerId = linkId ?: error("Beer id required"),
                    ratingId = activityNumber ?: error("Rating id required"),
                    text = linkText ?: error("Text required"),
                    timeEntered = timeEntered ?: error("Time required")
                )
            }
            else -> {
                Timber.w("Ignoring type $type")
                null
            }
        }
    }

    enum class Type(val id: Int) {
        BEER_ADDED(1),
        BEER_RATING(7),
        PLACE_RATING(8),
        IS_DRINKING(12),
        EVENT_ATTENDANCE(17),
        AWARD(18),
        PLACE_CHECK_IN(20),
        REACHED_RATINGS(21),
        BREWERY_ADDED(22);

        companion object {
            fun fromId(id: Int?): Type? = entries.find { it.id == id }
        }
    }

    companion object {
        val merger: Merger<FeedDataItem> = { old, new ->
            FeedDataItem(
                id = new.id,
                userId = new.userId ?: old.userId,
                username = new.username ?: old.username,
                type = new.type ?: old.type,
                linkId = new.linkId ?: old.linkId,
                linkText = new.linkText ?: old.linkText,
                activityNumber = new.activityNumber ?: old.activityNumber,
                timeEntered = new.timeEntered?.orLater(old.timeEntered),
                numComments = new.numComments ?: old.numComments
            )
        }
    }
}
