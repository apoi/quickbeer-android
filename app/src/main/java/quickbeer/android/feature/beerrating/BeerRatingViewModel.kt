package quickbeer.android.feature.beerrating

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import quickbeer.android.R
import quickbeer.android.data.result.Result
import quickbeer.android.data.state.State
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.store.RatingStore
import quickbeer.android.domain.ratinglist.repository.UserAllRatingsRepository
import quickbeer.android.domain.user.repository.CurrentUserRepository
import quickbeer.android.feature.beerdetails.model.RatingAction
import quickbeer.android.feature.beerrating.model.BeerRatingViewEvent
import quickbeer.android.feature.beerrating.model.BeerRatingViewEvent.ShowError
import quickbeer.android.feature.beerrating.model.BeerRatingViewEvent.ShowMessageAndClose
import quickbeer.android.feature.beerrating.model.BeerRatingViewState
import quickbeer.android.feature.beerrating.usecase.PublishRatingUseCase
import quickbeer.android.util.SingleLiveEvent

@HiltViewModel
class BeerRatingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ratingStore: RatingStore,
    private val publishRatingUseCase: PublishRatingUseCase,
    private val currentUserRepository: CurrentUserRepository,
    private val userAllRatingsRepository: UserAllRatingsRepository
) : ViewModel() {

    private val action = savedStateHandle.get<RatingAction>("action")!!

    private val _viewState = MutableStateFlow<State<BeerRatingViewState>>(State.Loading())
    val viewState: StateFlow<State<BeerRatingViewState>> = _viewState

    private val _events = SingleLiveEvent<BeerRatingViewEvent>()
    val events: LiveData<BeerRatingViewEvent> = _events

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val user = currentUserRepository.get() ?: error("User missing")
            val rating = action.ratingId
                ?.let { ratingStore.get(it) }
                ?: Rating.createDraft(action.beerId, user)

            _viewState.emit(State.from(BeerRatingViewState.create(rating)))
        }
    }

    fun setRating(rating: Rating) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.emit(updateState { it.copy(rating = rating) })
        }
    }

    fun publish(rating: Rating) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.emit(updateState { it.copy(isPublishing = true) })

            val result = publishRatingUseCase.publish(rating)
            val event = when (result) {
                is Result.Success -> ShowMessageAndClose(R.string.rating_publish_success)
                is Result.Failure -> ShowError(result.cause)
            }

            if (result is Result.Failure) {
                // Success closes the view so re-enable only in the case of failure
                _viewState.emit(updateState { it.copy(isPublishing = false) })
            }
            _events.postValue(event)
        }
    }

    fun saveDraft(rating: Rating) {
        viewModelScope.launch(Dispatchers.IO) {
            // We expect local saving to never fail
            _viewState.emit(updateState { it.copy(isSavingDraft = true) })
            publishRatingUseCase.saveDraft(rating)
            _events.postValue(ShowMessageAndClose(R.string.rating_draft_success))
        }
    }

    private fun updateState(
        block: (BeerRatingViewState) -> BeerRatingViewState
    ): State<BeerRatingViewState> {
        return when (val state = _viewState.value) {
            is State.Success -> State.Success(block(state.value))
            else -> error("Invalid state $state")
        }
    }
}
