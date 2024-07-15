package quickbeer.android.domain.brewerlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.ItemListStore
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.idlist.IdList

abstract class BrewerListStore(
    indexMapper: IndexMapper<String>,
    indexStoreCore: StoreCore<String, IdList>,
    brewerStoreCore: StoreCore<Int, Brewer>
) : ItemListStore<String, Int, Brewer>(
    indexMapper = indexMapper,
    getKey = Brewer::id,
    indexCore = indexStoreCore,
    valueCore = brewerStoreCore
)
