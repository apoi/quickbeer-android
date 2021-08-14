package quickbeer.android.domain.beerlist.store

import javax.inject.Inject
import quickbeer.android.data.store.StoreCore
import quickbeer.android.domain.beer.store.BeerStoreCore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.inject.IdListPersistedCore

class TickedBeersStore @Inject constructor(
    @IdListPersistedCore indexStoreCore: StoreCore<String, IdList>,
    beerStoreCore: BeerStoreCore
) : BeerListStore(INDEX_PREFIX, indexStoreCore, beerStoreCore) {

    companion object {
        private const val INDEX_PREFIX = "ticks/"
    }
}
