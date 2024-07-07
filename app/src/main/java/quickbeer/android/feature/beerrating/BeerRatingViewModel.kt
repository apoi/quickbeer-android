package quickbeer.android.feature.beerrating

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import quickbeer.android.data.state.State
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.ratinglist.repository.UserRatingRepository
import quickbeer.android.domain.user.repository.CurrentUserRepository
import quickbeer.android.feature.beerdetails.model.RatingAction

@HiltViewModel
class BeerRatingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val currentUserRepository: CurrentUserRepository,
    private val userRatingRepository: UserRatingRepository
) : ViewModel() {

    private val action = savedStateHandle.get<RatingAction>("action")!!

    private val _ratingState = MutableStateFlow<State<Rating>>(State.Initial)
    val ratingState: Flow<State<Rating>> = _ratingState

    private val _publishState = MutableStateFlow<State<Unit>>(State.Initial)
    val publishState: Flow<State<Unit>> = _publishState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val ratings = userRatingRepository.get()
            val user = currentUserRepository.get() ?: error("User missing")

            val rating = ratings
                ?.firstOrNull { it.id == action.ratingId }
                ?: Rating.createDraft(action.beerId, user)

            _ratingState.emit(State.from(rating))
        }
    }

    suspend fun publish(rating: Rating): State<Unit> {
        return State.Error(IllegalStateException("Not implemented"))
    }

    suspend fun saveDraft(rating: Rating): State<Unit> {
        // We expect local saving to never fail
        userRatingRepository.saveDraft(rating)
        return State.Success(Unit)
    }
}
