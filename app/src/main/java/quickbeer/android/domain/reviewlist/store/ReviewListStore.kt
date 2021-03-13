package quickbeer.android.domain.reviewlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.ItemListStore
import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.store.ReviewStoreCore
import quickbeer.android.domain.reviewlist.ReviewIdList
import quickbeer.android.domain.reviewlist.ReviewPage

abstract class ReviewListStore(
    indexStoreCore: StoreCore<ReviewPage, ReviewIdList>,
    reviewStoreCore: ReviewStoreCore
) : ItemListStore<ReviewPage, Int, Review>(
    indexMapper = Identity(),
    getKey = Review::id,
    indexCore = indexStoreCore,
    valueCore = reviewStoreCore
)
