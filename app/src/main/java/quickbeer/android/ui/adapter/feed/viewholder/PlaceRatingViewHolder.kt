package quickbeer.android.ui.adapter.feed.viewholder

import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.feed.FeedListModel

class PlaceRatingViewHolder(
    private val binding: FeedListItemBinding
) : FeedViewHolder(binding, LinkType.EXTERNAL) {

    override fun bind(item: FeedListModel, scope: CoroutineScope) {
        super.bind(item, scope)
        setUser(item.feedItem.username)
        setPlace(item.feedItem)
        binding.line3.isVisible = false
    }

    override fun setUser(username: String) {
        super.setUser(username)
        binding.line1.text = "$username reviewed"
    }

    private fun setPlace(item: FeedItem) {
        PLACE_RATING_PATTERN.find(item.linkText)
            ?.destructured
            ?.let { (place) ->
                binding.line2.text = place.trim()
            }
    }

    companion object {
        private val PLACE_RATING_PATTERN = "<a .*>(.*)<\\/a>".toRegex()
    }
}
