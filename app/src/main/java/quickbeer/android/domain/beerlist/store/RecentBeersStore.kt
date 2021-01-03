package quickbeer.android.domain.beerlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.domain.beer.store.BeerStoreCore
import quickbeer.android.domain.idlist.IdList

class RecentBeersStore(
    indexStoreCore: StoreCore<String, IdList>,
    beerStoreCore: BeerStoreCore
) : SingleBeerListStore(KEY, indexStoreCore, beerStoreCore) {

    companion object {
        private const val KEY = "recentBeers"
    }
}
