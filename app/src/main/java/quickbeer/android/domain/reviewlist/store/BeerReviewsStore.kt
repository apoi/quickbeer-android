package quickbeer.android.domain.reviewlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.ItemListStore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.domain.review.Review

class BeerReviewsStore(
    indexStoreCore: StoreCore<String, IdList>,
    reviewStoreCore: StoreCore<Int, Review>
) : ItemListStore<String, Int, Review>(
    indexMapper = Identity(),
    getKey = Review::id,
    indexCore = indexStoreCore,
    valueCore = reviewStoreCore
)
