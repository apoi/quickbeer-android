package quickbeer.android.domain.placelist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.ItemListStore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.domain.place.Place

abstract class PlaceListStore(
    indexMapper: IndexMapper<String>,
    indexStoreCore: StoreCore<String, IdList>,
    placeStoreCore: StoreCore<Int, Place>
) : ItemListStore<String, Int, Place>(
    indexMapper = indexMapper,
    getKey = Place::id,
    indexCore = indexStoreCore,
    valueCore = placeStoreCore
)
