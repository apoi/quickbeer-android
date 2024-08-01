package quickbeer.android.ui.adapter.feed.viewholder

import kotlinx.coroutines.CoroutineScope
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.feed.FeedListModel

class BrewerAddedViewHolder(
    private val binding: FeedListItemBinding
) : FeedViewHolder(binding) {

    override fun bind(item: FeedListModel, scope: CoroutineScope) {
        bindBrewer(item, scope)
        setUser(item.feedItem.username)
    }

    override fun setBrewer(item: FeedItem, brewer: Brewer) {
        super.setBrewer(item, brewer)

        val details = brewer.city?.let { "from $it" }

        binding.line1.text = "${item.username} added a new brewery"
        binding.line2.text = brewer.name
        if (details != null) {
            binding.line3.text = "from $details"
        }
    }
}
