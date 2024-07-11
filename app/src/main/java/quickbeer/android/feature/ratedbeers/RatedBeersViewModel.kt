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
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.ListCountValidator
import quickbeer.android.data.repository.NoFetch
import quickbeer.android.data.state.State
import quickbeer.android.domain.ratinglist.repository.UserAllRatingsRepository
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.repository.CurrentUserRepository
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListModelRatingTimeMapper

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class RatedBeersViewModel @Inject constructor(
    private val userRepository: CurrentUserRepository,
    private val userRatingsRepository: UserAllRatingsRepository,
    private val beerListMapper: BeerListModelRatingTimeMapper
) : ViewModel() {

    private val _viewState = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val viewState: StateFlow<State<List<BeerListModel>>> = _viewState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getStream(NoFetch())
                .filterIsInstance<State.Success<User>>()
                .map { it.value }
                .flatMapLatest { user ->
                    userRatingsRepository
                        .getStream(ListCountValidator { it >= (user.rateCount ?: 0) })
                        .map(beerListMapper::map)
                        .onStart { emit(State.Loading()) }
                }
                .collectLatest(_viewState::emit)
        }
    }
}
