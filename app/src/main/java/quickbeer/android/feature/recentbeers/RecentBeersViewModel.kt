package quickbeer.android.feature.recentbeers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.store.RecentBeersStore
import quickbeer.android.ui.adapter.beer.BeerListModel

class RecentBeersViewModel(
    private val recentBeersStore: RecentBeersStore,
    private val photoStore: BeerRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<List<BeerListModel>>()
    val viewState: LiveData<List<BeerListModel>> = _viewState

    init {
        viewModelScope.launch {
            getRecentBeers()
                .collect { _viewState.postValue(it) }
        }
    }

    private fun getRecentBeers(): Flow<List<BeerListModel>> {
        return recentBeersStore.getStream()
            .map { beers -> beers.map { BeerListModel(it.id, photoStore) } }
    }
}
