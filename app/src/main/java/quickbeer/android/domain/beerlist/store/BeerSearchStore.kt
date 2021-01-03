package quickbeer.android.domain.beerlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.domain.beer.store.BeerStoreCore
import quickbeer.android.domain.idlist.IdList

class BeerSearchStore(
    indexStoreCore: StoreCore<String, IdList>,
    private val beerStoreCore: BeerStoreCore
) : BeerListStore(INDEX_PREFIX, indexStoreCore, beerStoreCore) {

    fun search(query: String) = beerStoreCore.search(query)

    companion object {
        const val INDEX_PREFIX = "beerSearch/"
    }
}
