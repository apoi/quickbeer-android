package quickbeer.android.ui.adapter.beer

import javax.inject.Inject
import quickbeer.android.data.state.StateMapper
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.usecase.GetCurrentUserBeerRatingUseCase

class BeerListModelRatingMapper @Inject constructor(
    private val beerRepository: BeerRepository,
    private val getRatingUseCase: GetCurrentUserBeerRatingUseCase
) : StateMapper<List<Beer>, List<BeerListModel>>(
    { list ->
        list.sortedWith(compareByDescending(Beer::averageRating).thenBy(Beer::id))
            .map { BeerListModel(it.id, beerRepository, getRatingUseCase) }
    }
)

class BeerListModelRateCountMapper @Inject constructor(
    private val beerRepository: BeerRepository,
    private val getRatingUseCase: GetCurrentUserBeerRatingUseCase
) : StateMapper<List<Beer>, List<BeerListModel>>(
    { list ->
        list.sortedWith(
            compareByDescending(Beer::rateCount)
                .thenByDescending(Beer::averageRating)
                .thenBy(Beer::name)
                .thenBy(Beer::id)
        ).map { BeerListModel(it.id, beerRepository, getRatingUseCase) }
    }
)

class BeerListModelTickDateMapper @Inject constructor(
    private val beerRepository: BeerRepository,
    private val getRatingUseCase: GetCurrentUserBeerRatingUseCase
) : StateMapper<List<Beer>, List<BeerListModel>>(
    { list ->
        list.sortedWith(
            compareByDescending(Beer::tickDate)
                .thenBy(Beer::name)
                .thenBy(Beer::id)
        ).map { BeerListModel(it.id, beerRepository, getRatingUseCase) }
    }
)

class BeerListModelRatingTimeMapper @Inject constructor(
    private val beerRepository: BeerRepository,
    private val getRatingUseCase: GetCurrentUserBeerRatingUseCase
) : StateMapper<List<Rating>, List<BeerListModel>>(
    { list ->
        list.sortedWith(compareByDescending(Rating::timeUpdated))
            .mapNotNull { it.beerId }
            .map { BeerListModel(it, beerRepository, getRatingUseCase) }
    }
)

class BeerListModelAlphabeticalMapper @Inject constructor(
    private val beerRepository: BeerRepository,
    private val getRatingUseCase: GetCurrentUserBeerRatingUseCase
) : StateMapper<List<Beer>, List<BeerListModel>>(
    { list ->
        list.sortedWith(compareBy(Beer::name, Beer::id))
            .map { BeerListModel(it.id, beerRepository, getRatingUseCase) }
    }
)
