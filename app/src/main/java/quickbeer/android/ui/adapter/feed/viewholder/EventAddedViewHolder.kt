package quickbeer.android.ui.adapter.feed.viewholder

import kotlinx.coroutines.CoroutineScope
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.feed.FeedListModel

class EventAddedViewHolder(
    private val binding: FeedListItemBinding
) : FeedViewHolder(binding, LinkType.EXTERNAL) {

    override fun bind(item: FeedListModel, scope: CoroutineScope) {
        super.bind(item, scope)
        setUser(item.feedItem.username)
        setEvent(item.feedItem)
    }

    override fun setUser(username: String) {
        super.setUser(username)
        binding.line1.text = "$username added"
    }

    private fun setEvent(item: FeedItem) {
        EVENT_ADDED_PATTERN.find(item.linkText)
            ?.destructured
            ?.let { (event, details) ->
                binding.line2.text = event.trim()
                binding.line3.text = details.trim()
            }
    }

    companion object {
        private val EVENT_ADDED_PATTERN = "<a .*>(.*)<\\/a>(.*)".toRegex()
    }
}
