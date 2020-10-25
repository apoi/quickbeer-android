package quickbeer.android.domain.recentbeers

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.SingleItemListStore
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.idlist.IdList

class RecentBeersStore(
    indexStoreCore: StoreCore<String, IdList>,
    beerStoreCore: StoreCore<Int, Beer>
) : SingleItemListStore<String, Int, Beer>(
    indexKey = "recentBeers",
    getKey = Beer::id,
    indexCore = indexStoreCore,
    valueCore = beerStoreCore
)
