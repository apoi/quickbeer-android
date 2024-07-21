package quickbeer.android.domain.feed.network

import quickbeer.android.domain.feed.FeedDataItem
import quickbeer.android.util.JsonMapper

object FeedItemListJsonMapper : JsonMapper<String, List<FeedDataItem>, List<FeedItemJson>> {

    override fun map(mode: String, source: List<FeedItemJson>): List<FeedDataItem> {
        return source.map(::mapItem)
    }

    private fun mapItem(source: FeedItemJson): FeedDataItem {
        return FeedDataItem(
            id = source.activityId,
            userId = source.userId,
            username = source.username,
            type = FeedDataItem.Type.fromId(source.type),
            linkId = source.linkId,
            linkText = source.linkText,
            activityNumber = source.activityNumber,
            timeEntered = source.timeEntered,
            numComments = source.numComments
        )
    }
}
