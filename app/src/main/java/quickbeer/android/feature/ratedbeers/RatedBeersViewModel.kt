package quickbeer.android.feature.ratedbeers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.ListCountValidator
import quickbeer.android.data.repository.NoFetch
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.ratinglist.repository.UserRatingRepository
import quickbeer.android.domain.user.repository.CurrentUserRepository
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListModelRatingTimeMapper

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class RatedBeersViewModel @Inject constructor(
    private val beerRepository: BeerRepository,
    private val userRepository: CurrentUserRepository,
    private val userRatingsRepository: UserRatingRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val viewState: StateFlow<State<List<BeerListModel>>> = _viewState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getStream(NoFetch())
                .mapNotNull { it.valueOrNull() }
                .flatMapLatest { user ->
                    userRatingsRepository
                        .getStream(ListCountValidator { it >= (user.rateCount ?: 0) })
                        .map(BeerListModelRatingTimeMapper(beerRepository)::map)
                        .onStart { emit(State.Loading()) }
                }
                .collectLatest(_viewState::emit)
        }
    }
}
