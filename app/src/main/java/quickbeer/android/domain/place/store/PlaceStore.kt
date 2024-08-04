package quickbeer.android.domain.place.store

import javax.inject.Inject
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.place.Place

class PlaceStore @Inject constructor(
    placeStoreCore: PlaceStoreCore
) : DefaultStore<Int, Place >(placeStoreCore)
