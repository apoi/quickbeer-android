package quickbeer.android.domain.style.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.room.Database
import quickbeer.android.data.store.core.RoomDaoProxy
import quickbeer.android.data.store.core.RoomStoreCore
import quickbeer.android.domain.style.Style

class StyleRoomCore @Inject constructor(
    database: Database
) : RoomStoreCore<Int, Style, StyleEntity>(
    StyleEntityMapper,
    StyleDaoProxy(database.styleDao())
)

private class StyleDaoProxy(
    private val dao: StyleDao,
) : RoomDaoProxy<Int, StyleEntity>() {

    override suspend fun get(key: Int) = dao.get(key)

    override suspend fun get(keys: List<Int>) = dao.get(keys)

    override fun getStream(key: Int) = dao.getStream(key)

    override suspend fun getAll() = dao.getAll()

    override fun getAllStream() = dao.getAllStream()

    override suspend fun getKeys(): List<Int> = dao.getKeys()

    override fun getKeysStream(): Flow<List<Int>> = dao.getKeysStream()

    override suspend fun put(key: Int, value: StyleEntity) = dao.put(value)

    override suspend fun put(items: Map<Int, StyleEntity>) = dao.put(items.values.toList())

    override suspend fun delete(key: Int) = dao.delete(key) > 0
}
