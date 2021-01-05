package quickbeer.android.feature.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.Constants
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.data.state.StateListMapper
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.repository.BeerSearchRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.brewerlist.repository.BrewerSearchRepository
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.stylelist.repository.StyleListRepository
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListModelRateCountMapper
import quickbeer.android.ui.adapter.brewer.BrewerListModel
import quickbeer.android.ui.adapter.brewer.BrewerListModelAlphabeticMapper
import quickbeer.android.ui.adapter.style.StyleListModel
import quickbeer.android.ui.adapter.suggestion.SuggestionListModel
import quickbeer.android.ui.search.SearchActionsHandler

open class SearchViewModel(
    private val beerRepository: BeerRepository,
    private val beerSearchRepository: BeerSearchRepository,
    private val brewerRepository: BrewerRepository,
    private val brewerSearchRepository: BrewerSearchRepository,
    private val styleListRepository: StyleListRepository,
    private val countryRepository: CountryRepository
) : ViewModel(), SearchActionsHandler {

    private val query = MutableStateFlow("")

    private val _beerResults = MutableLiveData<State<List<BeerListModel>>>()
    val beerResults: LiveData<State<List<BeerListModel>>> = _beerResults

    private val _brewerResults = MutableLiveData<State<List<BrewerListModel>>>()
    val brewerResults: LiveData<State<List<BrewerListModel>>> = _brewerResults

    private val _styleResults = MutableLiveData<State<List<StyleListModel>>>()
    val styleResults: LiveData<State<List<StyleListModel>>> = _styleResults

    private val _suggestions = MutableStateFlow<List<SuggestionListModel>>(emptyList())
    override val suggestions: StateFlow<List<SuggestionListModel>> get() = _suggestions

    init {
        searchBeers()
        searchBrewers()
        searchStyles()
    }

    private fun searchBeers() {
        viewModelScope.launch(Dispatchers.IO) {
            beerSearchRepository
                .getStream(
                    query, { it.length >= Constants.QUERY_MIN_LENGTH },
                    Accept(), SEARCH_DELAY
                )
                // Avoid resorting the results if the set of beers didn't change
                .distinctUntilChanged { old, new -> sameIds(old, new, Beer::id) }
                .map(BeerListModelRateCountMapper(beerRepository)::map)
                .collect { _beerResults.postValue(it) }
        }
    }

    private fun searchBrewers() {
        viewModelScope.launch(Dispatchers.IO) {
            brewerSearchRepository
                .getStream(
                    query, { it.length >= Constants.QUERY_MIN_LENGTH },
                    Accept(), SEARCH_DELAY
                )
                .distinctUntilChanged { old, new -> sameIds(old, new, Brewer::id) }
                .map(BrewerListModelAlphabeticMapper(brewerRepository, countryRepository)::map)
                .collect { _brewerResults.postValue(it) }
        }
    }

    private fun searchStyles() {
        viewModelScope.launch(Dispatchers.IO) {
            query
                .flatMapLatest { query ->
                    styleListRepository.getStream(Accept())
                        .map { filterStyles(it, query) }
                }
                .map(StateListMapper(::StyleListModel)::map)
                .collect { _styleResults.postValue(it) }
        }
    }

    override fun onSearchChanged(query: String) {
        this.query.value = query.trim()
    }

    private fun filterStyles(state: State<List<Style>>, query: String): State<List<Style>> {
        if (state !is State.Success) return state

        return state.value.filter { it.parent != null && it.parent > 0 }
            .filter { matcher(query, it.name) }
            .sortedBy(Style::name)
            .let { State.from(it) }
    }

    private fun <T> sameIds(a: State<List<T>>, b: State<List<T>>, getId: (T) -> Int): Boolean {
        return a is State.Success && b is State.Success &&
            a.value.map(getId).toSet() == b.value.map(getId).toSet()
    }

    private fun matcher(query: String, value: String?): Boolean {
        if (value == null) return false

        return query.split(" ")
            .all { value.contains(it, ignoreCase = true) }
    }

    companion object {
        private const val SEARCH_DELAY = 500L
    }
}
