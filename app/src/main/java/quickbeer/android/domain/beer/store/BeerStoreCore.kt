package quickbeer.android.domain.beer.store

import quickbeer.android.data.room.Database
import quickbeer.android.data.store.core.CachingStoreCore
import quickbeer.android.domain.beer.Beer

class BeerStoreCore(
    private val roomCore: BeerRoomCore
) : CachingStoreCore<Int, Beer>(roomCore, Beer::id, Beer.merger) {

    constructor(database: Database) : this(BeerRoomCore(database))

    fun search(query: String) = roomCore.search(query)

    fun tickedBeers() = roomCore.tickedBeers()

    suspend fun clearTicks() = roomCore.clearTicks()

    fun lastAccessed() = roomCore.lastAccessed()
}
