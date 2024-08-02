package quickbeer.android.feature.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.AlwaysFetch
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.feed.repository.FeedRepository
import quickbeer.android.ui.adapter.feed.FeedListModel
import quickbeer.android.util.ktx.mapState
import quickbeer.android.util.ktx.mapStateList
import quickbeer.android.util.ktx.mode

@HiltViewModel
class FeedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: FeedRepository,
    beerRepository: BeerRepository,
    brewerRepository: BrewerRepository
) : ViewModel() {

    private val mode = savedStateHandle.mode()

    private val _feedListState = MutableStateFlow<State<List<FeedListModel>>>(State.Initial)
    val feedListState: StateFlow<State<List<FeedListModel>>> = _feedListState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Feed changes fast, so always fetch
            repository.getStream(mode.toString(), AlwaysFetch())
                .mapState { feedItems ->
                    feedItems.filter { it.type.isSupported() }
                }
                .mapStateList { FeedListModel(it, beerRepository, brewerRepository) }
                .collectLatest { _feedListState.emit(it) }
        }
    }
}
