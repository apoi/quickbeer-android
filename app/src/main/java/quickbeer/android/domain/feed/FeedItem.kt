package quickbeer.android.domain.feed

import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.store.Merger
import quickbeer.android.util.ktx.orLater

data class FeedItem(
    val id: Int,
    val type: Type,
    val userId: Int,
    val username: String,
    val linkId: Int?,
    val linkText: String,
    val activityNumber: Int?,
    val timeEntered: ZonedDateTime?,
    val numComments: Int?
) {

    fun beerId(): Int {
        return when (type) {
            Type.BEER_ADDED, Type.BEER_RATING -> linkId?.takeIf { it > 0 } ?: error("Invalid id")
            else -> error("No id for type $type")
        }
    }

    enum class Type(val id: Int) {
        BEER_ADDED(1),
        EVENT_ADDED(3),
        BEER_RATING(7),
        PLACE_RATING(8),
        IS_DRINKING(12),
        EVENT_ATTENDANCE(17),
        AWARD(18),
        PLACE_CHECK_IN(20),
        REACHED_RATINGS(21),
        BREWERY_ADDED(22),
        UNKNOWN(-1);

        fun isSupported(): Boolean {
            return when (this) {
                BEER_ADDED,
                BEER_RATING,
                REACHED_RATINGS,
                EVENT_ADDED,
                EVENT_ATTENDANCE -> true
                else -> false
            }
        }

        companion object {
            fun fromId(id: Int?): Type = entries.find { it.id == id } ?: UNKNOWN
        }
    }

    companion object {
        val merger: Merger<FeedItem> = { old, new ->
            FeedItem(
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
