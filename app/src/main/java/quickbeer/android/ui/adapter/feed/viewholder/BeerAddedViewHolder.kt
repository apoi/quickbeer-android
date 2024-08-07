package quickbeer.android.ui.adapter.feed.viewholder

import kotlinx.coroutines.CoroutineScope
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.feed.FeedListModel

class BeerAddedViewHolder(
    private val binding: FeedListItemBinding
) : FeedViewHolder(binding, LinkType.INTERNAL) {

    override fun bind(item: FeedListModel, scope: CoroutineScope) {
        super.bind(item, scope)
        setUser(item.feedItem.username)
        setBeerName(item.feedItem)
        bindBeer(item, scope)
    }

    private fun setBeerName(item: FeedItem) {
        BEER_ADDED_PATTERN.find(item.linkText)
            ?.destructured
            ?.let { (beer) -> binding.line2.text = beer }
    }

    override fun setBeer(item: FeedItem, beer: Beer) {
        super.setBeer(item, beer)

        binding.line1.text = "${item.username} added ${beer.styleName}"
        binding.line2.text = beer.name
        binding.line3.text = "from ${beer.brewerName}"
    }

    companion object {
        private val BEER_ADDED_PATTERN = "<a .*>(.*)<\\/a>".toRegex()
    }
}
