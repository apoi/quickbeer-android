package quickbeer.android.feature.tickedbeers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.repository.TickedBeersRepository
import quickbeer.android.domain.beerlist.repository.TickedBeersRepository.TickCountValidator
import quickbeer.android.domain.user.store.UserStore
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListModelTickDateMapper
import quickbeer.android.util.ktx.navId

@HiltViewModel
class TickedBeersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tickedBeersRepository: TickedBeersRepository,
    private val beerRepository: BeerRepository,
    private val userStore: UserStore
) : ViewModel() {

    private val userId = savedStateHandle.navId()

    private val _viewState = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val viewState: StateFlow<State<List<BeerListModel>>> = _viewState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tickedBeersRepository
                .getStream(userId.toString(), TickCountValidator(userId, userStore))
                .map(BeerListModelTickDateMapper(beerRepository)::map)
                .collectLatest(_viewState::emit)
        }
    }
}
