package quickbeer.android.domain.brewerlist.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import quickbeer.android.data.store.SingleStore
import quickbeer.android.domain.brewer.store.BrewerStoreCore

class RecentBrewersStore @Inject constructor(
    private val brewerStoreCore: BrewerStoreCore
) : SingleStore<List<Int>> {

    override suspend fun get(): List<Int> {
        return brewerStoreCore.lastAccessed().first()
    }

    override fun getStream(): Flow<List<Int>> {
        return brewerStoreCore.lastAccessed()
    }

    override suspend fun put(value: List<Int>): Boolean {
        error("This store is read only")
    }

    override suspend fun delete(): Boolean {
        error("This store is read only")
    }
}
