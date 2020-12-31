package quickbeer.android.domain.beerlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.idlist.IdList

class BeersInStyleStore(
    indexStoreCore: StoreCore<String, IdList>,
    beerStoreCore: StoreCore<Int, Beer>
) : BeerListStore(INDEX_PREFIX, indexStoreCore, beerStoreCore) {

    companion object {
        const val INDEX_PREFIX = "style/"
    }
}