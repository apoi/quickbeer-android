package quickbeer.android.ui.adapter.beer

import quickbeer.android.data.state.StateMapper
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository

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

class BeerListModelAlphabeticMapper(private val beerRepository: BeerRepository) :
    StateMapper<List<Beer>, List<BeerListModel>>(
        { list ->
            list.sortedWith(compareBy(Beer::name, Beer::id))
                .map { BeerListModel(it.id, beerRepository) }
        }
    )
