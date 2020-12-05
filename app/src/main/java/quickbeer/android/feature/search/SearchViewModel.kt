package quickbeer.android.feature.search

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
import quickbeer.android.domain.beersearch.repository.BeerSearchRepository
import quickbeer.android.domain.beersearch.repository.TopBeersRepository
import quickbeer.android.feature.shared.adapter.BeerListModel
import quickbeer.android.ui.adapter.search.SearchResult
import quickbeer.android.ui.adapter.search.SearchResultTypeFactory
import quickbeer.android.ui.adapter.simple.ListAdapter
import quickbeer.android.ui.search.SearchBarInterface
import timber.log.Timber

class SearchViewModel(
    query: String,
    private val repository: BeerSearchRepository,
    private val beerStore: BeerRepository
) : ViewModel(), SearchBarInterface {

    private val _viewState = MutableLiveData<State<List<BeerListModel>>>()
    val viewState: LiveData<State<List<BeerListModel>>> = _viewState

    private val searchAdapter = ListAdapter<SearchResult>(SearchResultTypeFactory())

    init {
        viewModelScope.launch {
            search(query)
        }
    }

    private suspend fun search(query: String) {
        repository.getStream(query, Accept())
            .map(StateListMapper<Beer, BeerListModel> { BeerListModel(it.id, beerStore) }::map)
            .collect { _viewState.postValue(it) }
    }

    override fun getSearchAdapter(): ListAdapter<SearchResult> {
        return searchAdapter
    }

    override fun onSearchChanged(query: String) {
        // TODO
    }

    override fun onSearchSubmit(query: String) {
        // TODO
    }

    override fun onSuggestionClicked(position: Int) {
        // TODO
    }

    override fun getSuggestionText(position: Int): String {
        // TODO
        return ""
     }
}
