package quickbeer.android.domain.reviewlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.domain.review.store.ReviewStoreCore

class ReviewsForBeerStore(
    indexStoreCore: StoreCore<String, IdList>,
    reviewStoreCore: ReviewStoreCore
) : ReviewListStore(INDEX_PREFIX, indexStoreCore, reviewStoreCore) {

    companion object {
        const val INDEX_PREFIX = "beerReviews/"
    }
}
