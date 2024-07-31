package quickbeer.android.ui.adapter.feed.viewholder

import kotlinx.coroutines.CoroutineScope
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.feed.FeedListModel

class PlaceRatingViewHolder(
    private val binding: FeedListItemBinding
) : FeedViewHolder(binding) {

    override fun bind(item: FeedListModel, scope: CoroutineScope) {
        setUser(item.feedItem.username)
        setPlace(item.feedItem)
    }

    override fun setUser(username: String) {
        super.setUser(username)
        binding.line1.text = "$username reviewed"
    }

    private fun setPlace(item: FeedItem) {
        val match = PLACE_RATING_PATTERN.find(item.linkText)!!
        val (place) = match.destructured

        binding.line2.text = place.trim()
    }

    companion object {
        private val PLACE_RATING_PATTERN = ".*<a .*>(.*)<\\/a>".toRegex()
    }
}
