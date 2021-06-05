package quickbeer.android.feature.recentbeers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.store.RecentBeersStore
import quickbeer.android.ui.adapter.beer.BeerListModel

@HiltViewModel
class RecentBeersViewModel @Inject constructor(
    private val recentBeersStore: RecentBeersStore,
    private val beerRepository: BeerRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<List<BeerListModel>>(emptyList())
    val viewState: Flow<List<BeerListModel>> = _viewState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getRecentBeers()
                .collectLatest(_viewState::emit)
        }
    }

    private fun getRecentBeers(): Flow<List<BeerListModel>> {
        return recentBeersStore.getStream()
            .map { beers -> beers.map { BeerListModel(it.id, beerRepository) } }
    }
}
