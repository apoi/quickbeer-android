package quickbeer.android.domain.rating.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import quickbeer.android.data.repository.NoFetch
import quickbeer.android.data.state.State
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.repository.UserBeerRatingRepository
import quickbeer.android.domain.user.repository.CurrentUserRepository

class GetCurrentUserBeerRatingUseCase @Inject constructor(
    private val ratingRepository: UserBeerRatingRepository,
    private val currentUserRepository: CurrentUserRepository
) {

    fun getCurrentUserRatingForBeer(beerId: Int): Flow<State<Rating>> {
        return currentUserRepository.getStream(NoFetch())
            .distinctUntilChangedBy { it.valueOrNull()?.id }
            .map { it.valueOrNull() }
            .flatMapLatest { user ->
                if (user != null) {
                    // Use only local ratings to avoid request spamming when scrolling lists
                    ratingRepository.getStream(Pair(user, beerId), NoFetch())
                } else {
                    flowOf(State.Empty)
                }
            }
            .onStart { emit(State.Loading()) }
            .distinctUntilChanged()
    }
}
