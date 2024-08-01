package quickbeer.android.ui.adapter.feed.viewholder

import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.feed.FeedListModel

class PlaceCheckInViewHolder(
    private val binding: FeedListItemBinding
) : FeedViewHolder(binding) {

    override fun bind(item: FeedListModel, scope: CoroutineScope) {
        setUser(item.feedItem.username)
        setPlace(item.feedItem)
        binding.line3.isVisible = false
    }

    override fun setUser(username: String) {
        super.setUser(username)
        binding.line1.text = "$username checked in at"
    }

    private fun setPlace(item: FeedItem) {
        PLACE_CHECK_IN_PATTERN.find(item.linkText)
            ?.destructured
            ?.let { (place) ->
                binding.line2.text = place.trim()
            }
    }

    companion object {
        private val PLACE_CHECK_IN_PATTERN = ".*<a .*>(.*)<\\/a>".toRegex()
    }
}
