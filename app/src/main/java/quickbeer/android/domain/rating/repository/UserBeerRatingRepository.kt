package quickbeer.android.domain.rating.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.repository.Repository
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.UserBeerRatingFetcher
import quickbeer.android.domain.rating.store.RatingStore
import quickbeer.android.domain.ratinglist.store.UserRatingsStore
import quickbeer.android.domain.user.User
import quickbeer.android.network.result.ApiResult

class UserBeerRatingRepository @Inject constructor(
    private val ratingStore: RatingStore,
    private val userRatingsStore: UserRatingsStore,
    private val ratingFetcher: UserBeerRatingFetcher
) : Repository<Pair<User, Int>, Rating>() {

    override suspend fun fetchRemote(key: Pair<User, Int>): ApiResult<Rating> {
        return ratingFetcher.fetch(key)
    }

    override suspend fun getLocal(key: Pair<User, Int>): Rating? {
        return userRatingsStore.get(key.first.id, key.second)
    }

    override fun getLocalStream(key: Pair<User, Int>): Flow<Rating?> {
        return userRatingsStore.getStream(key.first.id, key.second)
    }

    override suspend fun persist(key: Pair<User, Int>, value: Rating) {
        ratingStore.put(value.id, value)
    }
}
