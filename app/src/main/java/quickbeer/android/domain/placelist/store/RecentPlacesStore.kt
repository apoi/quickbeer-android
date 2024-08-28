package quickbeer.android.domain.placelist.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import quickbeer.android.data.store.SingleStore
import quickbeer.android.domain.place.store.PlaceStoreCore

class RecentPlacesStore @Inject constructor(
    private val placeStoreCore: PlaceStoreCore
) : SingleStore<List<Int>> {

    override suspend fun get(): List<Int> {
        return placeStoreCore.lastAccessed().first()
    }

    override fun getStream(): Flow<List<Int>> {
        return placeStoreCore.lastAccessed()
    }

    override suspend fun put(value: List<Int>): Boolean {
        error("This store is read only")
    }

    override suspend fun delete(): Boolean {
        error("This store is read only")
    }
}
