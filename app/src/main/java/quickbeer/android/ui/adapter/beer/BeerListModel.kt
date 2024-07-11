package quickbeer.android.ui.adapter.beer

import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.usecase.GetCurrentUserBeerRatingUseCase
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory

class BeerListModel(
    val beerId: Int,
    private val beerRepository: BeerRepository,
    private val getRatingUseCase: GetCurrentUserBeerRatingUseCase
) : ListItem {

    override fun id(): Long {
        return beerId.toLong()
    }

    override fun type(factory: ListTypeFactory): Int {
        return factory.type(this)
    }

    fun getBeer(): Flow<State<Beer>> {
        return beerRepository.getStream(beerId, Beer.BasicDataValidator())
    }

    fun getRating(): Flow<State<Rating>> {
        return getRatingUseCase.getCurrentUserRatingForBeer(beerId)
    }
}
