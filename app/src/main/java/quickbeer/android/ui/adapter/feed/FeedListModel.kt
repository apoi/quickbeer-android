package quickbeer.android.ui.adapter.feed

import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.domain.rating.Rating
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory

class FeedListModel(
    val feedItem: FeedItem,
    private val beerRepository: BeerRepository
) : ListItem {

    override fun id() = feedItem.id.toLong()

    override fun type(factory: ListTypeFactory) = factory.type(this)

    fun getBeer(beerId: Int): Flow<State<Beer>> {
        return beerRepository.getStream(beerId, Beer.BasicDataValidator())
    }

    fun getRating(ratingId: Int): Flow<State<Rating>> {
        TODO()
    }
}
