package quickbeer.android.domain.beerlist.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import quickbeer.android.data.store.SingleStore
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.store.BeerStoreCore

class TickedBeersStore @Inject constructor(
    private val beerStoreCore: BeerStoreCore
) : SingleStore<List<Beer>> {

    override suspend fun get(): List<Beer> {
        return beerStoreCore.tickedBeers().first()
    }

    override fun getStream(): Flow<List<Beer>> {
        return beerStoreCore.tickedBeers()
    }

    override suspend fun delete(): Boolean {
        beerStoreCore.clearTicks()
        return true
    }

    override suspend fun put(value: List<Beer>): Boolean {
        return beerStoreCore.put(value.associateBy { it.id }).isNotEmpty()
    }
}
