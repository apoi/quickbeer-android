package quickbeer.android.ui.adapter.feed.viewholder

import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.ui.adapter.feed.FeedListModel

class ReachedRatingsViewHolder(
    private val binding: FeedListItemBinding
) : FeedViewHolder(binding, LinkType.NONE) {

    override fun bind(item: FeedListModel, scope: CoroutineScope) {
        super.bind(item, scope)
        setUser(item.feedItem.username)
        binding.line2.text = item.feedItem.linkText
        binding.line3.isVisible = false
    }

    override fun setUser(username: String) {
        super.setUser(username)
        binding.line1.text = username
    }
}
