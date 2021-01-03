package quickbeer.android.domain.brewer.store

import quickbeer.android.data.room.Database
import quickbeer.android.data.store.core.CachingStoreCore
import quickbeer.android.domain.brewer.Brewer

class BrewerStoreCore(
    private val roomCore: BrewerRoomCore
) : CachingStoreCore<Int, Brewer>(roomCore, Brewer::id, Brewer.merger) {

    constructor(database: Database) : this(BrewerRoomCore(database))

    fun search(query: String) = roomCore.search(query)
}
