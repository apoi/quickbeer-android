package quickbeer.android.feature.topbeers

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
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.data.state.StateListMapper
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.repository.TopBeersRepository
import quickbeer.android.domain.rating.usecase.GetCurrentUserBeerRatingUseCase
import quickbeer.android.ui.adapter.beer.BeerListModel

@HiltViewModel
class TopBeersViewModel @Inject constructor(
    topBeersRepository: TopBeersRepository,
    beerRepository: BeerRepository,
    getRatingUseCase: GetCurrentUserBeerRatingUseCase
) : ViewModel() {

    private val _beerListState = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val beerListState: StateFlow<State<List<BeerListModel>>> = _beerListState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            topBeersRepository.getStream(Accept())
                .onStart { emit(State.Loading()) }
                .map(
                    StateListMapper<Beer, BeerListModel> {
                        BeerListModel(it.id, beerRepository, getRatingUseCase)
                    }::map
                )
                .collectLatest(_beerListState::emit)
        }
    }
}
