package quickbeer.android.domain.beersearch.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.ItemListStore
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.idlist.IdList

class BeerSearchStore(
    indexStoreCore: StoreCore<String, IdList>,
    beerStoreCore: StoreCore<Int, Beer>
) : ItemListStore<String, Int, Beer>(
    getKey = Beer::id,
    indexCore = indexStoreCore,
    valueCore = beerStoreCore
)
