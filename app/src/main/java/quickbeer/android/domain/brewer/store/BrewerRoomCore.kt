package quickbeer.android.domain.brewer.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import quickbeer.android.data.room.Database
import quickbeer.android.data.store.core.RoomDaoProxy
import quickbeer.android.data.store.core.RoomStoreCore
import quickbeer.android.domain.brewer.Brewer

class BrewerRoomCore @Inject constructor(
    val database: Database
) : RoomStoreCore<Int, Brewer, BrewerEntity>(
    BrewerEntityMapper,
    BrewerDaoProxy(database.brewerDao())
) {

    @Suppress("MagicNumber")
    fun search(query: String): Flow<List<Brewer>> {
        val parts = query.split(" ")
        val tail = parts.drop(3)

        // Use three first terms directly in search, filter others programmatically
        return database.brewerDao().search(parts.first(), parts.getOrNull(1), parts.getOrNull(2))
            .map { it.filter { entity -> contains(entity, tail) } }
            .map { it.map(BrewerEntityMapper::mapTo) }
    }

    fun lastAccessed(): Flow<List<Int>> {
        return database.brewerDao().lastAccessed()
    }

    private fun contains(entity: BrewerEntity, queries: List<String>): Boolean {
        return queries.all { q -> entity.normalizedName?.contains(q) == true }
    }
}

private class BrewerDaoProxy(
    private val dao: BrewerDao
) : RoomDaoProxy<Int, BrewerEntity>() {

    override suspend fun get(key: Int) = dao.get(key)

    override suspend fun get(keys: List<Int>) = dao.get(keys)

    override fun getStream(key: Int) = dao.getStream(key).filterNotNull()

    override suspend fun getAll() = dao.getAll()

    override fun getAllStream() = dao.getAllStream()

    override suspend fun getKeys(): List<Int> = dao.getKeys()

    override fun getKeysStream(): Flow<List<Int>> = dao.getKeysStream()

    override suspend fun put(key: Int, value: BrewerEntity) = dao.put(value)

    override suspend fun put(items: Map<Int, BrewerEntity>) = dao.put(items.values.toList())

    override suspend fun delete(key: Int) = dao.delete(key) > 0

    override suspend fun deleteAll() = dao.deleteAll() > 0
}
