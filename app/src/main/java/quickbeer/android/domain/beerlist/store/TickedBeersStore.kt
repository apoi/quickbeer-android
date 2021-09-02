package quickbeer.android.domain.beerlist.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.store.StoreCore
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.store.BeerStoreCore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.inject.IdListMemoryCore

class TickedBeersStore @Inject constructor(
    @IdListMemoryCore indexStoreCore: StoreCore<String, IdList>,
    private val beerStoreCore: BeerStoreCore
) : BeerListStore(INDEX_PREFIX, indexStoreCore, beerStoreCore) {

    fun getLocalTicks(): Flow<List<Beer>> {
        return beerStoreCore.tickedBeers()
    }

    suspend fun clearTicks() {
        beerStoreCore.clearTicks()
    }

    companion object {
        private const val INDEX_PREFIX = "ticks/"
    }
}
