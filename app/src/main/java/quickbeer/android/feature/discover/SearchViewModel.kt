package quickbeer.android.feature.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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
import quickbeer.android.util.exception.AppException
import timber.log.Timber

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
        // Launch searches
        val remoteRequest = query
            .filter { it.length >= Constants.QUERY_MIN_LENGTH }
            .debounce(SEARCH_DELAY)
            .flatMapLatest { query -> beerSearchRepository.getStream(query, Accept()) }
            .onStart { emit(State.Empty) }

        val localRequest = beerRepository.store.getStream()
            .distinctUntilChanged { a, b -> sameIds(a.map(Beer::id), b.map(Beer::id)) }

        // Beer results
        viewModelScope.launch(Dispatchers.IO) {
            combine(query, localRequest, remoteRequest) { q, local, remote ->
                combineResult(q, local, remote, Beer::name)
            }
                .map(BeerListModelRateCountMapper(beerRepository)::map)
                .collect { _beerResults.postValue(it) }
        }
    }

    private fun searchBrewers() {
        val remoteRequest = query
            .filter { it.length >= Constants.QUERY_MIN_LENGTH }
            .debounce(SEARCH_DELAY)
            .flatMapLatest { query -> brewerSearchRepository.getStream(query, Accept()) }
            .onStart { emit(State.Empty) }

        val localRequest = brewerRepository.store.getStream()
            .distinctUntilChanged { a, b -> sameIds(a.map(Brewer::id), b.map(Brewer::id)) }

        viewModelScope.launch(Dispatchers.IO) {
            combine(query, localRequest, remoteRequest) { q, local, remote ->
                combineResult(q, local, remote, Brewer::name)
            }
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

    private fun <T> combineResult(
        query: String,
        local: List<T>,
        remote: State<List<T>>,
        matcherField: (T) -> String?
    ): State<List<T>> {
        Timber.w("CT: " + Thread.currentThread())

        return if (query.isEmpty()) {
            State.Error(AppException.NoSearchEntered())
        } else if (query.length < Constants.QUERY_MIN_LENGTH) {
            State.Error(AppException.QueryTooShortException())
        } else {
            val result = local.filter { matcher(query, matcherField(it)) }
            when {
                remote is State.Loading -> State.Loading(result)
                result.isEmpty() && remote is State.Success -> State.Loading()
                else -> State.from(result)
            }
        }
    }

    override fun onSearchChanged(query: String) {
        this.query.value = query.trim()
    }

    private fun sameIds(a: List<Int>, b: List<Int>): Boolean {
        return a.toSet() == b.toSet()
    }

    private fun filterStyles(state: State<List<Style>>, query: String): State<List<Style>> {
        if (state !is State.Success) return state

        return state.value.filter { it.parent != null && it.parent > 0 }
            .filter { matcher(query, it.name) }
            .sortedBy(Style::name)
            .let { State.from(it) }
    }

    private fun matcher(query: String, value: String?): Boolean {
        return query.split(" ")
            .all { value?.contains(it, ignoreCase = true) == true }
    }

    companion object {
        private const val SEARCH_DELAY = 500L
    }
}
