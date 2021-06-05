package quickbeer.android.domain.review.store

import javax.inject.Inject
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.review.Review

class ReviewStore @Inject constructor(
    beerStoreCore: ReviewStoreCore
) : DefaultStore<Int, Review>(beerStoreCore)
