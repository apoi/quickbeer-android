package quickbeer.android.domain.place.store

import quickbeer.android.data.room.Database
import quickbeer.android.data.store.core.CachingStoreCore
import quickbeer.android.domain.place.Place

class PlaceStoreCore(
    private val roomCore: PlaceRoomCore
) : CachingStoreCore<Int, Place>(roomCore, Place::id) {

    constructor(database: Database) : this(PlaceRoomCore(database))

    fun search(query: String) = roomCore.search(query)

    fun lastAccessed() = roomCore.lastAccessed()
}
