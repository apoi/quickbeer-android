package quickbeer.android.domain.brewer.store

import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.room.Database
import quickbeer.android.data.store.core.RoomDaoProxy
import quickbeer.android.data.store.core.RoomStoreCore
import quickbeer.android.domain.brewer.Brewer

class BrewerRoomCore(
    database: Database
) : RoomStoreCore<Int, Brewer, BrewerEntity>(
    BrewerEntityMapper,
    BrewerDaoProxy(database.brewerDao())
)

private class BrewerDaoProxy(
    private val dao: BrewerDao,
) : RoomDaoProxy<Int, BrewerEntity>() {

    override suspend fun get(key: Int) = dao.get(key)

    override suspend fun get(keys: List<Int>) = dao.get(keys)

    override fun getStream(key: Int) = dao.getStream(key)

    override suspend fun getAll() = dao.getAll()

    override fun getAllStream() = dao.getAllStream()

    override suspend fun getKeys(): List<Int> = dao.getKeys()

    override fun getKeysStream(): Flow<List<Int>> = dao.getKeysStream()

    override suspend fun put(key: Int, value: BrewerEntity) = dao.put(value)

    override suspend fun put(items: Map<Int, BrewerEntity>) = dao.put(items.values.toList())

    override suspend fun delete(key: Int) = dao.delete(key) > 0
}
