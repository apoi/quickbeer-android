package quickbeer.android.domain.reviewlist.store

import javax.inject.Inject
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.ItemListStore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.store.ReviewStoreCore
import quickbeer.android.inject.IdListMemoryCore

class BeerReviewsStore @Inject constructor(
    @IdListMemoryCore indexStoreCore: StoreCore<String, IdList>,
    reviewStoreCore: ReviewStoreCore
) : ItemListStore<String, Int, Review>(
    indexMapper = Identity(),
    getKey = Review::id,
    indexCore = indexStoreCore,
    valueCore = reviewStoreCore
)