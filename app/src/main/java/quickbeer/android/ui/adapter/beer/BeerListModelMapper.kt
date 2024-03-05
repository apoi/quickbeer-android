package quickbeer.android.ui.adapter.beer

import quickbeer.android.data.state.StateMapper
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.rating.Rating

class BeerListModelRatingMapper(private val beerRepository: BeerRepository) :
    StateMapper<List<Beer>, List<BeerListModel>>(
        { list ->
            list.sortedWith(compareByDescending(Beer::averageRating).thenBy(Beer::id))
                .map { BeerListModel(it.id, beerRepository) }
        }
    )

class BeerListModelRateCountMapper(private val beerRepository: BeerRepository) :
    StateMapper<List<Beer>, List<BeerListModel>>(
        { list ->
            list.sortedWith(
                compareByDescending(Beer::rateCount)
                    .thenByDescending(Beer::averageRating)
                    .thenBy(Beer::name)
                    .thenBy(Beer::id)
            ).map { BeerListModel(it.id, beerRepository) }
        }
    )

class BeerListModelTickDateMapper(private val beerRepository: BeerRepository) :
    StateMapper<List<Beer>, List<BeerListModel>>(
        { list ->
            list.sortedWith(
                compareByDescending(Beer::tickDate)
                    .thenBy(Beer::name)
                    .thenBy(Beer::id)
            ).map { BeerListModel(it.id, beerRepository) }
        }
    )

class BeerListModelRatingTimeMapper(private val beerRepository: BeerRepository) :
    StateMapper<List<Rating>, List<BeerListModel>>(
        { list ->
            list.sortedWith(compareByDescending(Rating::timeUpdated))
                .mapNotNull { it.beerId }
                .map { BeerListModel(it, beerRepository) }
        }
    )

class BeerListModelAlphabeticMapper(private val beerRepository: BeerRepository) :
    StateMapper<List<Beer>, List<BeerListModel>>(
        { list ->
            list.sortedWith(compareBy(Beer::name, Beer::id))
                .map { BeerListModel(it.id, beerRepository) }
        }
    )
