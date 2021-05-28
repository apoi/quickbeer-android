package quickbeer.android.feature.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.repository.TopBeersRepository
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListModelRatingMapper

class DiscoverViewModel(
    private val repository: TopBeersRepository,
    private val beerRepository: BeerRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<State<List<BeerListModel>>>(State.Empty)
    val viewState: StateFlow<State<List<BeerListModel>>> = _viewState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getStream(Accept())
                .map(BeerListModelRatingMapper(beerRepository)::map)
                .collectLatest(_viewState::emit)
        }
    }
}
