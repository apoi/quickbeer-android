package quickbeer.android.feature.beers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.recentbeers.RecentBeersStore
import quickbeer.android.feature.beers.adapter.BeerListModel
import timber.log.Timber

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
            .onEach { Timber.w("JUU " + it) }
            .map { beers -> beers.map { BeerListModel(it.id, photoStore) } }
    }
}
