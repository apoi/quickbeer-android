package quickbeer.android.ui.adapter.review

import quickbeer.android.data.state.StateMapper
import quickbeer.android.domain.review.Review

class ReviewListModelDateSortingMapper :
    StateMapper<List<Review>, List<ReviewListModel>>(
        { list ->
            list.sortedWith(compareByDescending(Review::timeEntered).thenBy(Review::id))
                .map(::ReviewListModel)
        }
    )
