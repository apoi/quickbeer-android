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
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.brewerlist.store.RecentBrewersStore
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.brewer.BrewerListModel

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    recentBeersStore: RecentBeersStore,
    beerRepository: BeerRepository,
    recentBrewersStore: RecentBrewersStore,
    brewerRepository: BrewerRepository,
    countryRepository: CountryRepository
) : ViewModel() {

    private val _recentBeersState = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val recentBeersState: StateFlow<State<List<BeerListModel>>> = _recentBeersState

    private val _recentBrewersState = MutableStateFlow<State<List<BrewerListModel>>>(State.Initial)
    val recentBrewersState: StateFlow<State<List<BrewerListModel>>> = _recentBrewersState

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

        viewModelScope.launch(Dispatchers.IO) {
            recentBrewersStore.getStream()
                .map { State.Success(it) }
                .onStart { State.Loading<List<Int>>() }
                .map(
                    StateListMapper<Int, BrewerListModel> {
                        BrewerListModel(it, null, brewerRepository, countryRepository)
                    }::map
                )
                .collectLatest(_recentBrewersState::emit)
        }
    }
}
