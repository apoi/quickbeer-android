package quickbeer.android.domain.review.store

import javax.inject.Inject
import quickbeer.android.data.store.core.CachingStoreCore
import quickbeer.android.domain.review.Review

class ReviewStoreCore @Inject constructor(
    roomCore: ReviewRoomCore
) : CachingStoreCore<Int, Review>(roomCore, Review::id, Review.merger)
