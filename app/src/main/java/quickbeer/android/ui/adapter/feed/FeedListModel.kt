package quickbeer.android.ui.adapter.feed

import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory

class FeedListModel(val feedItem: FeedItem) : ListItem {

    override fun id() = feedItem.id.toLong()

    override fun type(factory: ListTypeFactory) = factory.type(this)
}
