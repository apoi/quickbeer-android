package quickbeer.android.feature.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.repository.BeerSearchRepository
import quickbeer.android.domain.beerlist.store.BeerSearchStore
import quickbeer.android.feature.shared.adapter.BeerListModel
import quickbeer.android.feature.shared.adapter.BeerListModelAlphabeticMapper
import quickbeer.android.ui.adapter.search.SearchSuggestion
import quickbeer.android.ui.adapter.search.SearchSuggestion.Type
import quickbeer.android.ui.search.SearchActionsHandler

open class SearchViewModel(
    initialQuery: String?,
    private val searchRepository: BeerSearchRepository,
    private val beerRepository: BeerRepository,
    private val beerSearchStore: BeerSearchStore
) : ViewModel(), SearchActionsHandler {

    private val _viewState = MutableLiveData<State<List<BeerListModel>>>()
    val viewState: LiveData<State<List<BeerListModel>>> = _viewState

    private val _suggestions = MutableStateFlow<List<SearchSuggestion>>(emptyList())
    override val suggestions: StateFlow<List<SearchSuggestion>> get() = _suggestions

    init {
        if (initialQuery != null) {
            search(initialQuery)
        }

        registerQueries()
    }

    fun search(query: String) {
        viewModelScope.launch {
            searchRepository.getStream(query, Accept())
                .map(BeerListModelAlphabeticMapper(beerRepository)::map)
                .collect { _viewState.postValue(it) }
        }
    }

    private fun registerQueries() {
        viewModelScope.launch {
            beerSearchStore.getKeysStream()
                .map { queryList ->
                    queryList.map { query ->
                        SearchSuggestion(query.hashCode(), Type.SEARCH, query)
                    }
                }
                .collect { _suggestions.value = it }
        }
    }

    override fun onSearchChanged(query: String) {
        // TODO
    }
}
