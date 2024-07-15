package quickbeer.android.feature.countrylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.countrylist.repository.CountryListRepository
import quickbeer.android.ui.adapter.country.CountryListModel
import quickbeer.android.util.ktx.mapStateList

@HiltViewModel
class CountryListViewModel @Inject constructor(
    countryListRepository: CountryListRepository
) : ViewModel() {

    private val _countryListState = MutableStateFlow<State<List<CountryListModel>>>(State.Initial)
    val countryListState: StateFlow<State<List<CountryListModel>>> = _countryListState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            countryListRepository.getStream(Accept())
                .onStart { emit(State.Loading()) }
                .mapStateList(::CountryListModel)
                .collectLatest(_countryListState::emit)
        }
    }
}
