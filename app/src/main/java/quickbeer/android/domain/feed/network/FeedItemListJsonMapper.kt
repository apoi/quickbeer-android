package quickbeer.android.domain.feed.network

import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.util.JsonMapper

object FeedItemListJsonMapper : JsonMapper<String, List<FeedItem>, List<FeedItemJson>> {

    override fun map(mode: String, source: List<FeedItemJson>): List<FeedItem> {
        return source.map(::mapItem)
    }

    private fun mapItem(source: FeedItemJson): FeedItem {
        return FeedItem(
            id = source.activityId,
            userId = source.userId ?: 0,
            username = source.username.orEmpty(),
            type = FeedItem.Type.fromId(source.type),
            linkId = source.linkId,
            linkText = source.linkText.orEmpty(),
            activityNumber = source.activityNumber,
            timeEntered = source.timeEntered,
            numComments = source.numComments
        )
    }
}
