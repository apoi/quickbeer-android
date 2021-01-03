package quickbeer.android.domain.beerlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.domain.beer.store.BeerStoreCore
import quickbeer.android.domain.idlist.IdList

class BrewersBeersStore(
    indexStoreCore: StoreCore<String, IdList>,
    beerStoreCore: BeerStoreCore
) : BeerListStore(INDEX_PREFIX, indexStoreCore, beerStoreCore) {

    companion object {
        const val INDEX_PREFIX = "brewer/"
    }
}
