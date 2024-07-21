package quickbeer.android.ui.adapter.feed

import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.base.ListViewHolder

class FeedViewHolder(
    private val binding: FeedListItemBinding
) : ListViewHolder<FeedListModel>(binding.root) {

    override fun bind(item: FeedListModel) {
        when (item.feedItem.type) {
            FeedItem.Type.BEER_RATING -> bindBeerRating(item.feedItem)
            FeedItem.Type.BEER_ADDED -> TODO()
            FeedItem.Type.PLACE_RATING -> TODO()
            FeedItem.Type.IS_DRINKING -> TODO()
            FeedItem.Type.EVENT_ATTENDANCE -> TODO()
            FeedItem.Type.AWARD -> TODO()
            FeedItem.Type.PLACE_CHECK_IN -> TODO()
            FeedItem.Type.REACHED_RATINGS -> TODO()
            FeedItem.Type.BREWERY_ADDED -> TODO()
            null -> TODO()
        }
    }

    private fun bindBeerRating(item: FeedItem) {
    }
}
