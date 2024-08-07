package quickbeer.android.ui.adapter.feed

import android.view.ViewGroup
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.domain.feed.FeedItem.Type.BEER_ADDED
import quickbeer.android.domain.feed.FeedItem.Type.BEER_RATING
import quickbeer.android.domain.feed.FeedItem.Type.BREWERY_ADDED
import quickbeer.android.domain.feed.FeedItem.Type.EVENT_ADDED
import quickbeer.android.domain.feed.FeedItem.Type.EVENT_ATTENDANCE
import quickbeer.android.domain.feed.FeedItem.Type.PLACE_CHECK_IN
import quickbeer.android.domain.feed.FeedItem.Type.PLACE_RATING
import quickbeer.android.domain.feed.FeedItem.Type.REACHED_RATINGS
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory
import quickbeer.android.ui.adapter.base.ListViewHolder
import quickbeer.android.ui.adapter.feed.viewholder.BeerAddedViewHolder
import quickbeer.android.ui.adapter.feed.viewholder.BeerRatingViewHolder
import quickbeer.android.ui.adapter.feed.viewholder.BrewerAddedViewHolder
import quickbeer.android.ui.adapter.feed.viewholder.EventAddedViewHolder
import quickbeer.android.ui.adapter.feed.viewholder.EventAttendanceViewHolder
import quickbeer.android.ui.adapter.feed.viewholder.PlaceCheckInViewHolder
import quickbeer.android.ui.adapter.feed.viewholder.PlaceRatingViewHolder
import quickbeer.android.ui.adapter.feed.viewholder.ReachedRatingsViewHolder

class FeedTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return (item as FeedListModel).feedItem.type.id
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        val creator = { createBinding(FeedListItemBinding::inflate, parent) }

        return when (FeedItem.Type.fromId(type)) {
            BEER_ADDED -> BeerAddedViewHolder(creator())
            BEER_RATING -> BeerRatingViewHolder(creator())
            REACHED_RATINGS -> ReachedRatingsViewHolder(creator())
            EVENT_ADDED -> EventAddedViewHolder(creator())
            EVENT_ATTENDANCE -> EventAttendanceViewHolder(creator())
            PLACE_RATING -> PlaceRatingViewHolder(creator())
            PLACE_CHECK_IN -> PlaceCheckInViewHolder(creator())
            BREWERY_ADDED -> BrewerAddedViewHolder(creator())
            else -> error("Unsupported type $type")
        }
    }
}
