package quickbeer.android.domain.place.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import quickbeer.android.data.room.Database
import quickbeer.android.data.store.core.RoomDaoProxy
import quickbeer.android.data.store.core.RoomStoreCore
import quickbeer.android.domain.place.Place
import javax.inject.Inject

class PlaceRoomCore @Inject constructor(
    val database: Database
) : RoomStoreCore<Int, Place, PlaceEntity>(
    PlaceEntityMapper,
    PlaceDaoProxy(database.placeDao())
) {

    @Suppress("MagicNumber")
    fun search(query: String): Flow<List<Place>> {
        val parts = query.split(" ")
        val tail = parts.drop(3)

        // Use three first terms directly in search, filter others programmatically
        return database.placeDao().search(parts.first(), parts.getOrNull(1), parts.getOrNull(2))
            .map { it.filter { entity -> contains(entity, tail) } }
            .map { it.map(PlaceEntityMapper::mapTo) }
    }

    fun lastAccessed(): Flow<List<Int>> {
        return database.placeDao().lastAccessed()
    }

    private fun contains(entity: PlaceEntity, queries: List<String>): Boolean {
        return queries.all { q -> entity.normalizedName?.contains(q) == true }
    }
}


private class PlaceDaoProxy(
    private val dao: PlaceDao
) : RoomDaoProxy<Int, PlaceEntity>() {

    override suspend fun get(key: Int) = dao.get(key)

    override suspend fun get(keys: List<Int>) = dao.get(keys)

    override fun getStream(key: Int) = dao.getStream(key).filterNotNull()

    override suspend fun getAll() = dao.getAll()

    override fun getAllStream() = dao.getAllStream()

    override suspend fun getKeys(): List<Int> = dao.getKeys()

    override fun getKeysStream(): Flow<List<Int>> = dao.getKeysStream()

    override suspend fun put(key: Int, value: PlaceEntity) = dao.put(value)

    override suspend fun put(items: Map<Int, PlaceEntity>) = dao.put(items.values.toList())

    override suspend fun delete(key: Int) = dao.delete(key) > 0

    override suspend fun deleteAll() = dao.deleteAll() > 0
}
