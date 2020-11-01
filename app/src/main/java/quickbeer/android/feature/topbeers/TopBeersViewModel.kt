package quickbeer.android.feature.topbeers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beersearch.repository.TopBeersRepository
import quickbeer.android.feature.shared.adapter.BeerListModel
import timber.log.Timber

class TopBeersViewModel(
    private val repository: TopBeersRepository,
    private val beerStore: BeerRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<List<BeerListModel>>()
    val viewState: LiveData<List<BeerListModel>> = _viewState

    init {
        viewModelScope.launch {
            getRecentBeers()
        }
    }

    private suspend fun getRecentBeers() {
        repository.getStream(Accept())
            .collect {
                when (it) {
                    is State.Loading -> Unit
                    is State.Empty -> Unit
                    is State.Success -> {
                        val values = it.value.map { (id) -> BeerListModel(id, beerStore) }
                        _viewState.postValue(values)
                    }
                    is State.Error -> Unit
                }
            }
    }
}
