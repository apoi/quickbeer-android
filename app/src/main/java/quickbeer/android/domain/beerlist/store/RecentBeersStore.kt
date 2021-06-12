package quickbeer.android.domain.beerlist.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import quickbeer.android.data.store.SingleStore
import quickbeer.android.domain.beer.store.BeerStoreCore

class RecentBeersStore @Inject constructor(
    private val beerStoreCore: BeerStoreCore
) : SingleStore<List<Int>> {

    override suspend fun get(): List<Int> {
        return beerStoreCore.lastAccessed().first()
    }

    override fun getStream(): Flow<List<Int>> {
        return beerStoreCore.lastAccessed()
    }

    override suspend fun put(value: List<Int>): Boolean {
        error("This store is read only")
    }

    override suspend fun delete(): Boolean {
        error("This store is read only")
    }
}
