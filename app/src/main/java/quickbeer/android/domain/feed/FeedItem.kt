package quickbeer.android.domain.feed

import org.threeten.bp.ZonedDateTime
import quickbeer.android.Constants
import quickbeer.android.data.store.Merger
import quickbeer.android.data.store.StoreCore

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

    fun brewerId(): Int {
        return when (type) {
            Type.BREWERY_ADDED -> linkId?.takeIf { it > 0 } ?: error("Invalid id")
            else -> error("No id for type $type")
        }
    }

    fun link(): String? {
        return LINK_PATTERN.find(linkText)
            ?.destructured
            ?.component1()
            ?.let { Constants.API_URL + it }
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
                EVENT_ATTENDANCE,
                PLACE_RATING,
                PLACE_CHECK_IN,
                BREWERY_ADDED -> true
                AWARD,
                IS_DRINKING,
                UNKNOWN -> false
            }
        }

        companion object {
            fun fromId(id: Int?): Type = entries.find { it.id == id } ?: UNKNOWN
        }
    }

    companion object {
        val merger: Merger<FeedItem> = StoreCore.takeNew()
        val LINK_PATTERN = ".*<a href=&quot;(.*)&quot;>.*".toRegex()
    }
}
