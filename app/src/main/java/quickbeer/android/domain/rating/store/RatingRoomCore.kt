package quickbeer.android.domain.rating.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import quickbeer.android.data.room.Database
import quickbeer.android.data.store.core.RoomDaoProxy
import quickbeer.android.data.store.core.RoomStoreCore
import quickbeer.android.domain.rating.Rating

class RatingRoomCore @Inject constructor(
    private val database: Database
) : RoomStoreCore<Int, Rating, RatingEntity>(
    RatingEntityMapper,
    RatingDaoProxy(database.ratingDao())
) {

    suspend fun getRatingByUser(userId: Int, beerId: Int): Rating? {
        return database.ratingDao()
            .getRatingByUserStream(userId, beerId)
            .firstOrNull()
            ?.let(RatingEntityMapper::mapTo)
    }

    fun getRatingByUserStream(userId: Int, beerId: Int): Flow<Rating?> {
        return database.ratingDao()
            .getRatingByUserStream(userId, beerId)
            .map { entity -> entity?.let(RatingEntityMapper::mapTo) }
    }

    fun getAllRatingsByUserStream(userId: Int): Flow<List<Rating>> {
        return database.ratingDao()
            .getAllRatingsByUserStream(userId)
            .map { it.map(RatingEntityMapper::mapTo) }
    }
}

private class RatingDaoProxy(
    private val dao: RatingDao
) : RoomDaoProxy<Int, RatingEntity>() {

    override suspend fun get(key: Int) = dao.get(key)

    override suspend fun get(keys: List<Int>) = dao.get(keys)

    override fun getStream(key: Int) = dao.getStream(key).filterNotNull()

    override suspend fun getAll() = dao.getAll()

    override fun getAllStream() = dao.getAllStream()

    override suspend fun getKeys(): List<Int> = dao.getKeys()

    override fun getKeysStream(): Flow<List<Int>> = dao.getKeysStream()

    override suspend fun put(key: Int, value: RatingEntity) = dao.put(value)

    override suspend fun put(items: Map<Int, RatingEntity>) = dao.put(items.values.toList())

    override suspend fun delete(key: Int) = dao.delete(key) > 0

    override suspend fun deleteAll() = dao.deleteAll() > 0
}
