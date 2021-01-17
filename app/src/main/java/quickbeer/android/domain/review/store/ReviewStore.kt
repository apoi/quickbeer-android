package quickbeer.android.domain.review.store

import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.review.Review

class ReviewStore(
    beerStoreCore: ReviewStoreCore
) : DefaultStore<Int, Review>(beerStoreCore)
