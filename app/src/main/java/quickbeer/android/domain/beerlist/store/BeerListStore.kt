package quickbeer.android.domain.beerlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.ItemListStore
import quickbeer.android.data.store.store.SingleItemListStore
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.idlist.IdList

abstract class BeerListStore(
    indexMapper: IndexMapper<String>,
    indexStoreCore: StoreCore<String, IdList>,
    beerStoreCore: StoreCore<Int, Beer>
) : ItemListStore<String, Int, Beer>(
    indexMapper = indexMapper,
    getKey = Beer::id,
    indexCore = indexStoreCore,
    valueCore = beerStoreCore
)

abstract class SingleBeerListStore(
    indexKey: String,
    indexStoreCore: StoreCore<String, IdList>,
    beerStoreCore: StoreCore<Int, Beer>
) : SingleItemListStore<String, Int, Beer>(
    indexKey,
    getKey = Beer::id,
    indexCore = indexStoreCore,
    valueCore = beerStoreCore
)
