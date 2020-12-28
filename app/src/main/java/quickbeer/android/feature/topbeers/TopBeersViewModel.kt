package quickbeer.android.feature.topbeers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.repository.TopBeersRepository
import quickbeer.android.feature.shared.adapter.BeerListModel
import quickbeer.android.feature.shared.adapter.BeerListModelRatingMapper

class TopBeersViewModel(
    private val repository: TopBeersRepository,
    private val beerRepository: BeerRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<State<List<BeerListModel>>>()
    val viewState: LiveData<State<List<BeerListModel>>> = _viewState

    init {
        viewModelScope.launch {
            getRecentBeers()
        }
    }

    private suspend fun getRecentBeers() {
        repository.getStream(Accept())
            .map(BeerListModelRatingMapper(beerRepository)::map)
            .collect { _viewState.postValue(it) }
    }
}
