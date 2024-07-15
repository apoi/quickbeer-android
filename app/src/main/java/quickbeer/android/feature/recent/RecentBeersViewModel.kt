package quickbeer.android.feature.recent

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
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.store.RecentBeersStore
import quickbeer.android.domain.rating.usecase.GetCurrentUserBeerRatingUseCase
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.util.ktx.mapStateList

@HiltViewModel
class RecentBeersViewModel @Inject constructor(
    recentBeersStore: RecentBeersStore,
    beerRepository: BeerRepository,
    getRatingUseCase: GetCurrentUserBeerRatingUseCase
) : ViewModel() {

    private val _recentBeersState = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val recentBeersState: StateFlow<State<List<BeerListModel>>> = _recentBeersState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            recentBeersStore.getStream()
                .map { State.from(it) }
                .onStart { emit(State.Loading()) }
                .mapStateList { BeerListModel(it, beerRepository, getRatingUseCase) }
                .collectLatest(_recentBeersState::emit)
        }
    }
}
