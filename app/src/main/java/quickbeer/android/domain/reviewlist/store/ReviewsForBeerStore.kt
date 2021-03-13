package quickbeer.android.domain.reviewlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.domain.review.store.ReviewStoreCore
import quickbeer.android.domain.reviewlist.ReviewIdList
import quickbeer.android.domain.reviewlist.ReviewPage

class ReviewsForBeerStore(
    indexStoreCore: StoreCore<ReviewPage, ReviewIdList>,
    reviewStoreCore: ReviewStoreCore
) : ReviewListStore(indexStoreCore, reviewStoreCore)
