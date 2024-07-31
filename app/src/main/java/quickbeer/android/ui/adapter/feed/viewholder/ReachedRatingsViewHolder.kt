package quickbeer.android.ui.adapter.feed.viewholder

import kotlinx.coroutines.CoroutineScope
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.feed.FeedListModel

class ReachedRatingsViewHolder(
    private val binding: FeedListItemBinding
) : FeedViewHolder(binding) {

    override fun bind(item: FeedListModel, scope: CoroutineScope) {
        setUser(item.feedItem.username)
        binding.line2.text = item.feedItem.linkText
    }

    override fun setUser(username: String) {
        super.setUser(username)
        binding.line1.text = username
    }
}
