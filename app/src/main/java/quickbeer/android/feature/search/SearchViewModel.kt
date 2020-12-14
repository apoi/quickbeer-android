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
import quickbeer.android.domain.beersearch.store.BeerListStore
import quickbeer.android.feature.shared.adapter.BeerListModel
import quickbeer.android.feature.topbeers.TopBeersViewEffect
import quickbeer.android.ui.adapter.search.SearchResult
import quickbeer.android.ui.adapter.search.SearchResult.Type
import quickbeer.android.ui.adapter.search.SearchResultTypeFactory
import quickbeer.android.ui.adapter.simple.ListAdapter
import quickbeer.android.ui.search.SearchBarInterface
import quickbeer.android.util.SingleLiveEvent

class SearchViewModel(
    query: String,
    private val searchRepository: BeerSearchRepository,
    private val beerRepository: BeerRepository,
    private val beerListStore: BeerListStore
) : ViewModel(), SearchBarInterface {

    private val _viewState = MutableLiveData<State<List<BeerListModel>>>()
    val viewState: LiveData<State<List<BeerListModel>>> = _viewState

    private val _viewEffect = SingleLiveEvent<TopBeersViewEffect>()
    val viewEffect: LiveData<TopBeersViewEffect> = _viewEffect

    private val searchAdapter = ListAdapter<SearchResult>(SearchResultTypeFactory())

    init {
        search(query)
        registerQueries()
    }

    private fun search(query: String) {
        viewModelScope.launch {
            searchRepository.getStream(query, Accept())
                .map(
                    StateListMapper<Beer, BeerListModel> {
                        BeerListModel(it.id, beerRepository)
                    }::map
                )
                .collect { _viewState.postValue(it) }
        }
    }

    private fun registerQueries() {
        viewModelScope.launch {
            beerListStore.getKeysStream()
                .map { queryList ->
                    queryList.map { query -> SearchResult(query.hashCode(), Type.SEARCH, query) }
                }
                .collect { searchAdapter.setItems(it) }
        }
    }

    override fun getSearchAdapter(): ListAdapter<SearchResult> {
        return searchAdapter
    }

    override fun onSearchChanged(query: String) {
        // TODO
    }

    override fun onSearchSubmit(query: String) {
        search(query)
    }

    override fun onSuggestionClicked(position: Int) {
        // TODO
    }

    override fun getSuggestionText(position: Int): String {
        // TODO
        return ""
    }
}
