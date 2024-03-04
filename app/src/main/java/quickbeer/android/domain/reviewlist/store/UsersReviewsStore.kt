package quickbeer.android.domain.reviewlist.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import quickbeer.android.data.store.Store
import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.store.ReviewStoreCore

/**
 * Store that returns all persisted reviews for the user associated by an ID.
 */
class UsersReviewsStore @Inject constructor(
    private val reviewStoreCore: ReviewStoreCore
) : Store<Int, List<Review>> {

    override suspend fun get(): List<List<Review>> {
        return reviewStoreCore.getAll()
            .groupBy(Review::userId)
            .values.toList()
    }

    override suspend fun get(key: Int): List<Review>? {
        return getStream(key).firstOrNull()
    }

    override fun getStream(): Flow<List<List<Review>>> {
        return reviewStoreCore.getAllStream()
            .map { it.groupBy(Review::userId).values.toList() }
    }

    override fun getStream(key: Int): Flow<List<Review>> {
        return reviewStoreCore.reviewsForUser(key)
    }

    override suspend fun getKeys(): List<Int> {
        return reviewStoreCore.getAll()
            .mapNotNull { it.userId }
            .distinct()
    }

    override fun getKeysStream(): Flow<List<Int>> {
        return reviewStoreCore.getAllStream()
            .map { it.mapNotNull(Review::userId).distinct() }
    }

    override suspend fun put(key: Int, value: List<Review>): Boolean {
        return reviewStoreCore.put(value.associateBy { it.id }).isNotEmpty()
    }

    override suspend fun delete(key: Int): Boolean {
        error("Operation not supported")
    }
}
