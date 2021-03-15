package quickbeer.android.ui.adapter.review

import quickbeer.android.data.state.StateMapper
import quickbeer.android.domain.review.Review

class ReviewListModelUpdatedDateMapper :
    StateMapper<List<Review>, List<ReviewListModel>>(
        { list ->
            list.sortedWith(compareByDescending(Review::timeUpdated).thenBy(Review::id))
                .map(::ReviewListModel)
        }
    )
