package quickbeer.android.domain.idlist.store

import quickbeer.android.data.room.Database
import quickbeer.android.data.store.core.RoomDaoProxy
import quickbeer.android.data.store.core.RoomStoreCore
import quickbeer.android.domain.idlist.IdList

class IdListRoomCore(
    database: Database
) : RoomStoreCore<String, IdList, IdListEntity>(
    IdListEntityMapper,
    IdListDaoProxy(database.idListDao())
)

private class IdListDaoProxy(
    private val dao: IdListDao,
) : RoomDaoProxy<String, IdListEntity>() {

    override suspend fun get(key: String) = dao.get(key)

    override suspend fun get(keys: List<String>) = dao.get(keys)

    override fun getStream(key: String) = dao.getStream(key)

    override suspend fun getAll() = dao.getAll()

    override fun getAllStream() = dao.getAllStream()

    override suspend fun put(key: String, value: IdListEntity) = dao.put(value)

    override suspend fun put(items: Map<String, IdListEntity>) = dao.put(items.values.toList())

    override suspend fun delete(key: String) = dao.delete(key) > 0
}
