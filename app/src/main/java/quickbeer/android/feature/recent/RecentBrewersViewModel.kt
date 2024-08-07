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
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.brewerlist.store.RecentBrewersStore
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.ui.adapter.brewer.BrewerListModel
import quickbeer.android.util.ktx.mapStateList

@HiltViewModel
class RecentBrewersViewModel @Inject constructor(
    recentBrewersStore: RecentBrewersStore,
    brewerRepository: BrewerRepository,
    countryRepository: CountryRepository
) : ViewModel() {

    private val _recentBrewersState = MutableStateFlow<State<List<BrewerListModel>>>(State.Initial)
    val recentBrewersState: StateFlow<State<List<BrewerListModel>>> = _recentBrewersState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            recentBrewersStore.getStream()
                .map { State.from(it) }
                .onStart { emit(State.Loading()) }
                .mapStateList { BrewerListModel(it, null, brewerRepository, countryRepository) }
                .collectLatest(_recentBrewersState::emit)
        }
    }
}
