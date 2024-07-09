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
import quickbeer.android.data.state.State
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.BeerPublishRatingFetcher
import quickbeer.android.domain.ratinglist.repository.UserRatingRepository
import quickbeer.android.domain.user.repository.CurrentUserRepository
import quickbeer.android.feature.beerdetails.model.RatingAction
import quickbeer.android.feature.beerrating.model.BeerRatingViewEvent
import quickbeer.android.feature.beerrating.model.BeerRatingViewEvent.PublishError
import quickbeer.android.feature.beerrating.model.BeerRatingViewEvent.PublishSuccess
import quickbeer.android.feature.beerrating.model.BeerRatingViewEvent.SaveDraftSuccess
import quickbeer.android.feature.beerrating.model.BeerRatingViewState
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.SingleLiveEvent

@HiltViewModel
class BeerRatingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val currentUserRepository: CurrentUserRepository,
    private val userRatingRepository: UserRatingRepository,
    private val beerPublishRatingFetcher: BeerPublishRatingFetcher
) : ViewModel() {

    private val action = savedStateHandle.get<RatingAction>("action")!!

    private val _viewState = MutableStateFlow<State<BeerRatingViewState>>(State.Loading())
    val viewState: StateFlow<State<BeerRatingViewState>> = _viewState

    private val _events = SingleLiveEvent<BeerRatingViewEvent>()
    val events: LiveData<BeerRatingViewEvent> = _events

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val ratings = userRatingRepository.get()
            val user = currentUserRepository.get() ?: error("User missing")

            val rating = ratings
                ?.firstOrNull { it.id == action.ratingId }
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

            val result = beerPublishRatingFetcher.fetch(rating)
            val event = when (result) {
                is ApiResult.Success -> PublishSuccess
                is ApiResult.HttpError -> PublishError(result.cause)
                is ApiResult.NetworkError -> PublishError(result.cause)
                is ApiResult.UnknownError -> PublishError(result.cause)
            }

            if (event is PublishError) {
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
            userRatingRepository.saveDraft(rating)
            _events.postValue(SaveDraftSuccess)
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
