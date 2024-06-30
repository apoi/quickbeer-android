package quickbeer.android.feature.beerrating

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.NoFetch
import quickbeer.android.data.state.State
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.ratinglist.repository.UserRatingRepository
import quickbeer.android.util.ktx.navId

@HiltViewModel
class BeerRatingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRatingRepository: UserRatingRepository
) : ViewModel() {

    private val beerId = savedStateHandle.navId()

    private val _ratingState = MutableStateFlow<State<Rating>>(State.Initial)
    val ratingState: Flow<State<Rating>> = _ratingState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userRatingRepository.getStream(NoFetch())
                .collectLatest { ratings ->
                    val rating = ratings.valueOrNull()
                        ?.firstOrNull { it.beerId == beerId }

                    _ratingState.emit(State.from(rating))
                }
        }
    }
}
