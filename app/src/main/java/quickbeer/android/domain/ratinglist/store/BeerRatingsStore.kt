package quickbeer.android.domain.ratinglist.store

import javax.inject.Inject
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.ItemListStore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.store.RatingStoreCore
import quickbeer.android.inject.IdListMemoryCore

class BeerRatingsStore @Inject constructor(
    @IdListMemoryCore indexStoreCore: StoreCore<String, IdList>,
    ratingStoreCore: RatingStoreCore
) : ItemListStore<String, Int, Rating>(
    indexMapper = Identity(),
    getKey = Rating::id,
    indexCore = indexStoreCore,
    valueCore = ratingStoreCore
)