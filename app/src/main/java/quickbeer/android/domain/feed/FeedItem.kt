package quickbeer.android.domain.feed

import org.threeten.bp.ZonedDateTime

sealed class FeedItem(
    open val id: Int
) {

    data class BeerRated(
        override val id: Int,
        val userId: Int,
        val userName: String,
        val beerId: Int,
        val ratingId: Int,
        val text: String,
        val timeEntered: ZonedDateTime
    ) : FeedItem(id)
}
