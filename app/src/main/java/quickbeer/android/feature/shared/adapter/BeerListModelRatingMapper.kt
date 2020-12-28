package quickbeer.android.feature.shared.adapter

import quickbeer.android.data.state.StateMapper
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository

class BeerListModelRatingMapper(private val beerRepository: BeerRepository) :
    StateMapper<List<Beer>, List<BeerListModel>>(
        { list ->
            list.sortedByDescending(Beer::averageRating)
                .map { BeerListModel(it.id, beerRepository) }
        }
    )

class BeerListModelAlphabeticMapper(private val beerRepository: BeerRepository) :
    StateMapper<List<Beer>, List<BeerListModel>>(
        { list ->
            list.sortedBy(Beer::name)
                .map { BeerListModel(it.id, beerRepository) }
        }
    )
