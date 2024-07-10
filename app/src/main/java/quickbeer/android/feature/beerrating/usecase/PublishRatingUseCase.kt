package quickbeer.android.feature.beerrating.usecase

import javax.inject.Inject
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.result.Result
import quickbeer.android.data.state.State
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.BeerPublishRatingFetcher
import quickbeer.android.domain.rating.store.RatingStore
import quickbeer.android.domain.ratinglist.repository.UserBeerRatingRepository
import quickbeer.android.domain.user.repository.CurrentUserRepository
import quickbeer.android.network.result.ApiResult

class PublishRatingUseCase @Inject constructor(
    private val ratingStore: RatingStore,
    private val currentUserRepository: CurrentUserRepository,
    private val userBeerRatingRepository: UserBeerRatingRepository,
    private val publishFetcher: BeerPublishRatingFetcher
) {

    suspend fun saveDraft(rating: Rating) {
        if (!rating.isDraft()) error("Not a draft!")
        ratingStore.put(rating.id, rating)
    }

    suspend fun publish(rating: Rating): Result<Unit> {
        val result = publishRating(rating)

        return if (result is Result.Success) {
            updateRating(rating)
        } else {
            result
        }
    }

    private suspend fun publishRating(rating: Rating): Result<Unit> {
        val result = when (val apiResult = publishFetcher.fetch(rating)) {
            is ApiResult.Success -> Result.Success(Unit)
            is ApiResult.HttpError -> Result.Failure(apiResult.cause)
            is ApiResult.NetworkError -> Result.Failure(apiResult.cause)
            is ApiResult.UnknownError -> Result.Failure(apiResult.cause)
        }

        if (result is Result.Success) {
            if (rating.isDraft()) {
                ratingStore.delete(rating.id)
            }
        }

        return result
    }

    private suspend fun updateRating(rating: Rating): Result<Unit> {
        val beerId = rating.beerId ?: error("Missing beer id")
        val user = currentUserRepository.get() ?: error("Missing user")
        val state = userBeerRatingRepository.fetchAndPersist(Pair(user, beerId), Accept())

        return when (state) {
            is State.Success, State.Empty -> Result.Success(Unit)
            is State.Error -> Result.Failure(state.cause)
            else -> error("Invalid fetch state")
        }
    }
}
