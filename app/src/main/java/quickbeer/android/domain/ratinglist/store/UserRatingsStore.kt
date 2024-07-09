package quickbeer.android.domain.ratinglist.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import quickbeer.android.data.store.Store
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.store.RatingStoreCore

/**
 * Store that returns all persisted ratings for the user associated by an ID.
 */
class UserRatingsStore @Inject constructor(
    private val ratingStoreCore: RatingStoreCore
) : Store<Int, List<Rating>> {

    suspend fun get(userId: Int, beerId: Int): Rating? {
        return ratingStoreCore.ratingByUser(userId, beerId)
    }

    fun getStream(userId: Int, beerId: Int): Flow<Rating> {
        return ratingStoreCore.ratingByUserStream(userId, beerId)
    }

    override suspend fun get(): List<List<Rating>> {
        return ratingStoreCore.getAll()
            .groupBy(Rating::userId)
            .values.toList()
    }

    override suspend fun get(userId: Int): List<Rating>? {
        return getStream(userId).firstOrNull()
    }

    override fun getStream(): Flow<List<List<Rating>>> {
        return ratingStoreCore.getAllStream()
            .map { it.groupBy(Rating::userId).values.toList() }
    }

    override fun getStream(userId: Int): Flow<List<Rating>> {
        return ratingStoreCore.allRatingsByUser(userId)
    }

    override suspend fun getKeys(): List<Int> {
        return ratingStoreCore.getAll()
            .mapNotNull { it.userId }
            .distinct()
    }

    override fun getKeysStream(): Flow<List<Int>> {
        return ratingStoreCore.getAllStream()
            .map { it.mapNotNull(Rating::userId).distinct() }
    }

    override suspend fun put(userId: Int, value: List<Rating>): Boolean {
        // Clear stored ratings first in case any was deleted out of app
        delete(userId)
        return ratingStoreCore.put(value.associateBy { it.id }).isNotEmpty()
    }

    override suspend fun delete(userId: Int): Boolean {
        val ratings = get(userId)
        ratings?.forEach { ratingStoreCore.delete(it.id) }
        return ratings?.isNotEmpty() == true
    }

    override suspend fun deleteAll(): Boolean {
        return ratingStoreCore.deleteAll()
    }
}
