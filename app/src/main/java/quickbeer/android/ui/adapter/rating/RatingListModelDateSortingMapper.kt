package quickbeer.android.ui.adapter.rating

import quickbeer.android.data.state.StateMapper
import quickbeer.android.domain.rating.Rating

class RatingListModelDateSortingMapper :
    StateMapper<List<Rating>, List<RatingListModel>>(
        { list ->
            list.sortedWith(compareByDescending(Rating::timeEntered).thenBy(Rating::id))
                .map(::RatingListModel)
        }
    )
