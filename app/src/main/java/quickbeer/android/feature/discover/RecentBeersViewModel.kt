package quickbeer.android.feature.discover

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
import quickbeer.android.data.state.StateListMapper
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.store.RecentBeersStore
import quickbeer.android.ui.adapter.beer.BeerListModel

@HiltViewModel
class RecentBeersViewModel @Inject constructor(
    recentBeersStore: RecentBeersStore,
    beerRepository: BeerRepository,
) : ViewModel() {

    private val _recentBeersState = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val recentBeersState: StateFlow<State<List<BeerListModel>>> = _recentBeersState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            recentBeersStore.getStream()
                .map { State.Success(it) }
                .onStart { State.Loading<List<Int>>() }
                .map(
                    StateListMapper<Int, BeerListModel> {
                        BeerListModel(it, beerRepository)
                    }::map
                )
                .collectLatest(_recentBeersState::emit)
        }
    }
}
