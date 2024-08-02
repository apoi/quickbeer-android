package quickbeer.android.ui.adapter.feed.viewholder

import kotlinx.coroutines.CoroutineScope
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.feed.FeedListModel

class BeerRatingViewHolder(
    private val binding: FeedListItemBinding
) : FeedViewHolder(binding, LinkType.INTERNAL) {

    override fun bind(item: FeedListModel, scope: CoroutineScope) {
        super.bind(item, scope)
        setUser(item.feedItem.username)
        setBeerName(item.feedItem)
        bindBeer(item, scope)
    }

    override fun setUser(username: String) {
        super.setUser(username)

        binding.line1.text = "$username rated"
    }

    private fun setBeerName(item: FeedItem) {
        BEER_RATING_PATTERN.find(item.linkText)
            ?.destructured
            ?.let { (beer) -> binding.line2.text = beer }
    }

    override fun setBeer(item: FeedItem, beer: Beer) {
        super.setBeer(item, beer)

        val details = listOfNotNull(
            beer.styleName,
            beer.alcohol
                ?.takeIf { it > 0 }
                ?.toString()
                ?.let { "ABV %s%%".format(it) }
        )

        binding.line3.text = details.joinToString(", ")
    }

    companion object {
        private val BEER_RATING_PATTERN = "<a .*>(.*)<\\/a>".toRegex()
    }
}
