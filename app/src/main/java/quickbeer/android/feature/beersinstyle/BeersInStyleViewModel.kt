package quickbeer.android.feature.beersinstyle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.data.state.StateListMapper
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.repository.BeersInStyleRepository
import quickbeer.android.feature.shared.adapter.BeerListModel

class BeersInStyleViewModel(
    styleId: Int,
    private val repository: BeersInStyleRepository,
    private val beerStore: BeerRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<State<List<BeerListModel>>>()
    val viewState: LiveData<State<List<BeerListModel>>> = _viewState

    init {
        viewModelScope.launch {
            repository.getStream(styleId.toString(), Accept())
                .map(StateListMapper<Beer, BeerListModel> { BeerListModel(it.id, beerStore) }::map)
                .collect { _viewState.postValue(it) }
        }
    }
}
