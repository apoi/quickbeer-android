package quickbeer.android.feature.shared.adapter

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

class BeerListModelAlphabeticMapper(private val beerRepository: BeerRepository) :
    StateMapper<List<Beer>, List<BeerListModel>>(
        { list ->
            list.sortedWith(compareBy(Beer::name, Beer::id))
                .map { BeerListModel(it.id, beerRepository) }
        }
    )
