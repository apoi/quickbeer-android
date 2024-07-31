package quickbeer.android.ui.adapter.feed.viewholder

import kotlinx.coroutines.CoroutineScope
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.feed.FeedListModel

class BeerRatingViewHolder(
    private val binding: FeedListItemBinding
) : FeedViewHolder(binding) {

    override fun bind(item: FeedListModel, scope: CoroutineScope) {
        bindBeer(item, scope)
        setUser(item.feedItem.username)
    }

    override fun setUser(username: String) {
        super.setUser(username)

        binding.line1.text = "$username rated"
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

        binding.line2.text = beer.name
        binding.line3.text = details.joinToString(", ")
    }
}
