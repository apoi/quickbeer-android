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
import quickbeer.android.data.state.StateListMapper
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.repository.BeerSearchRepository
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.brewerlist.repository.BrewerSearchRepository
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.stylelist.repository.StyleListRepository
import quickbeer.android.feature.shared.adapter.beer.BeerListModel
import quickbeer.android.feature.shared.adapter.beer.BeerListModelAlphabeticMapper
import quickbeer.android.feature.shared.adapter.brewer.BrewerListModel
import quickbeer.android.feature.shared.adapter.brewer.BrewerListModelAlphabeticMapper
import quickbeer.android.feature.shared.adapter.style.StyleListModel
import quickbeer.android.ui.adapter.search.SearchSuggestion
import quickbeer.android.ui.adapter.search.SearchSuggestion.Type
import quickbeer.android.ui.search.SearchActionsHandler

open class SearchViewModel(
    private val beerRepository: BeerRepository,
    private val beerSearchRepository: BeerSearchRepository,
    private val brewerRepository: BrewerRepository,
    private val brewerSearchRepository: BrewerSearchRepository,
    private val styleListRepository: StyleListRepository
) : ViewModel(), SearchActionsHandler {

    private val _beerResults = MutableLiveData<State<List<BeerListModel>>>()
    val beerResults: LiveData<State<List<BeerListModel>>> = _beerResults

    private val _brewerResults = MutableLiveData<State<List<BrewerListModel>>>()
    val brewerResults: LiveData<State<List<BrewerListModel>>> = _brewerResults

    private val _styleResults = MutableLiveData<State<List<StyleListModel>>>()
    val styleResults: LiveData<State<List<StyleListModel>>> = _styleResults

    private val _suggestions = MutableStateFlow<List<SearchSuggestion>>(emptyList())
    override val suggestions: StateFlow<List<SearchSuggestion>> get() = _suggestions

    init {
        registerQueries()
    }

    fun search(query: String) {
        viewModelScope.launch {
            beerSearchRepository.getStream(query, Accept())
                .map(BeerListModelAlphabeticMapper(beerRepository)::map)
                .collect { _beerResults.postValue(it) }
        }

        viewModelScope.launch {
            brewerSearchRepository.getStream(query, Accept())
                .map(BrewerListModelAlphabeticMapper(brewerRepository)::map)
                .collect { _brewerResults.postValue(it) }
        }

        viewModelScope.launch {
            styleListRepository.getStream(Accept())
                .map { filterStyles(it, query) }
                .map(StateListMapper(::StyleListModel)::map)
                .collect { _styleResults.postValue(it) }
        }
    }

    private fun filterStyles(state: State<List<Style>>, query: String): State<List<Style>> {
        if (state !is State.Success) return state

        return state.value.filter { it.parent != null && it.parent > 0 }
            .filter { it.name.contains(query, ignoreCase = true) }
            .sortedBy(Style::name)
            .let { State.from(it) }
    }

    private fun registerQueries() {
        viewModelScope.launch {
            beerSearchRepository.store.getKeysStream()
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
