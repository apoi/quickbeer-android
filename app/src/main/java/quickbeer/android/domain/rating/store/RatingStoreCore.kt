package quickbeer.android.domain.rating.store

import javax.inject.Inject
import quickbeer.android.data.store.core.CachingStoreCore
import quickbeer.android.domain.rating.Rating

class RatingStoreCore @Inject constructor(
    private val roomCore: RatingRoomCore
) : CachingStoreCore<Int, Rating>(roomCore, Rating::id) {

    suspend fun ratingByUser(userId: Int, beerId: Int) = roomCore.getRatingByUser(userId, beerId)

    fun ratingByUserStream(userId: Int, beerId: Int) =
        roomCore.getRatingByUserStream(userId, beerId)

    fun allRatingsByUser(userId: Int) = roomCore.getAllRatingsByUserStream(userId)
}
