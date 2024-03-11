package quickbeer.android.feature.ratedbeers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.AlwaysFetch
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.ratinglist.repository.UserRatingRepository
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListModelRatingTimeMapper

@HiltViewModel
class RatedBeersViewModel @Inject constructor(
    private val beerRepository: BeerRepository,
    private val userRatingsRepository: UserRatingRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val viewState: StateFlow<State<List<BeerListModel>>> = _viewState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userRatingsRepository
                .getStream(AlwaysFetch())
                .map(BeerListModelRatingTimeMapper(beerRepository)::map)
                .onStart { emit(State.Loading()) }
                .collectLatest(_viewState::emit)
        }
    }
}
