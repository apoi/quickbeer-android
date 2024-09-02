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
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.domain.place.repository.PlaceRepository
import quickbeer.android.domain.placelist.store.RecentPlacesStore
import quickbeer.android.ui.adapter.place.PlaceListModel
import quickbeer.android.util.ktx.mapStateList

@HiltViewModel
class RecentPlacesViewModel @Inject constructor(
    recentPlacesStore: RecentPlacesStore,
    placeRepository: PlaceRepository,
    countryRepository: CountryRepository
) : ViewModel() {

    private val _recentPlacesState = MutableStateFlow<State<List<PlaceListModel>>>(State.Initial)
    val recentPlacesState: StateFlow<State<List<PlaceListModel>>> = _recentPlacesState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            recentPlacesStore.getStream()
                .map { State.from(it) }
                .onStart { emit(State.Loading()) }
                .mapStateList { PlaceListModel(it, placeRepository, countryRepository) }
                .collectLatest(_recentPlacesState::emit)
        }
    }
}
