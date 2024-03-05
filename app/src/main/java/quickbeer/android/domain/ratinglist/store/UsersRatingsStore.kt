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
class UsersRatingsStore @Inject constructor(
    private val ratingStoreCore: RatingStoreCore
) : Store<Int, List<Rating>> {

    override suspend fun get(): List<List<Rating>> {
        return ratingStoreCore.getAll()
            .groupBy(Rating::userId)
            .values.toList()
    }

    override suspend fun get(key: Int): List<Rating>? {
        return getStream(key).firstOrNull()
    }

    override fun getStream(): Flow<List<List<Rating>>> {
        return ratingStoreCore.getAllStream()
            .map { it.groupBy(Rating::userId).values.toList() }
    }

    override fun getStream(key: Int): Flow<List<Rating>> {
        return ratingStoreCore.ratingsForUser(key)
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

    override suspend fun put(key: Int, value: List<Rating>): Boolean {
        return ratingStoreCore.put(value.associateBy { it.id }).isNotEmpty()
    }

    override suspend fun delete(key: Int): Boolean {
        error("Operation not supported")
    }
}
