package quickbeer.android.domain.rating.store

import javax.inject.Inject
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.rating.Rating

class RatingStore @Inject constructor(
    ratingStoreCore: RatingStoreCore
) : DefaultStore<Int, Rating>(ratingStoreCore)
