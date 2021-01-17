package quickbeer.android.domain.review.store

import quickbeer.android.data.store.core.CachingStoreCore
import quickbeer.android.domain.review.Review

class ReviewStoreCore(
    roomCore: ReviewRoomCore
) : CachingStoreCore<Int, Review>(roomCore, Review::id, Review.merger)
