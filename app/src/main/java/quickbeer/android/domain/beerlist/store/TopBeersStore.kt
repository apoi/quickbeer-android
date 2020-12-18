package quickbeer.android.domain.beerlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.idlist.IdList

class TopBeersStore(
    indexStoreCore: StoreCore<String, IdList>,
    beerStoreCore: StoreCore<Int, Beer>
) : SingleBeerListStore(KEY, indexStoreCore, beerStoreCore) {

    companion object {
        private const val KEY = "top50"
    }
}
