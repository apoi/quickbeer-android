package quickbeer.android.feature.search

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.Constants
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.repository.Repository
import quickbeer.android.data.state.State
import quickbeer.android.data.state.StateListMapper
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.repository.BeerSearchRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.brewerlist.repository.BrewerSearchRepository
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.domain.countrylist.repository.CountryListRepository
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.stylelist.repository.StyleListRepository
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListModelRateCountMapper
import quickbeer.android.ui.adapter.brewer.BrewerListModel
import quickbeer.android.ui.adapter.brewer.BrewerListModelAlphabeticMapper
import quickbeer.android.ui.adapter.country.CountryListModel
import quickbeer.android.ui.adapter.style.StyleListModel
import quickbeer.android.util.ktx.normalize

open class SearchViewModel(
    private val beerRepository: BeerRepository,
    private val beerSearchRepository: BeerSearchRepository,
    private val brewerRepository: BrewerRepository,
    private val brewerSearchRepository: BrewerSearchRepository,
    private val styleListRepository: StyleListRepository,
    private val countryListRepository: CountryListRepository,
    private val countryRepository: CountryRepository
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val typeFlow = MutableStateFlow(SearchType.BEER)

    private val _beerResults = MutableLiveData<State<List<BeerListModel>>>()
    val beerResults: LiveData<State<List<BeerListModel>>> = _beerResults

    private val _brewerResults = MutableLiveData<State<List<BrewerListModel>>>()
    val brewerResults: LiveData<State<List<BrewerListModel>>> = _brewerResults

    private val _styleResults = MutableLiveData<State<List<StyleListModel>>>()
    val styleResults: LiveData<State<List<StyleListModel>>> = _styleResults

    private val _countryResults = MutableLiveData<State<List<CountryListModel>>>()
    val countryResults: LiveData<State<List<CountryListModel>>> = _countryResults

    private val queryLengthValidator = object : Repository.KeyValidator<String> {
        override fun isEmpty(key: String) = key.isEmpty()
        override fun isValid(key: String) = key.length >= Constants.QUERY_MIN_LENGTH
    }

    init {
        searchBeers()
        searchBrewers()
        searchStyles()
        searchCountries()
    }

    fun onSearchQueryChanged(query: String) {
        queryFlow.value = query.normalize()
    }

    fun onSearchTypeChanged(type: SearchType) {
        typeFlow.value = type
    }

    private fun searchBeers() {
        viewModelScope.launch(Dispatchers.IO) {
            val beerQueryFlow = queryFlow.combineTransform(typeFlow) { query, type ->
                if (type == SearchType.BEER) emit(query)
            }

            beerSearchRepository
                .getStream(beerQueryFlow, queryLengthValidator, Accept(), SEARCH_DELAY)
                // Avoid resorting the results if the set of beers didn't change
                .distinctUntilChanged { old, new -> sameIds(old, new, Beer::id) }
                .map(BeerListModelRateCountMapper(beerRepository)::map)
                .collect { _beerResults.postValue(it) }
        }
    }

    private fun searchBrewers() {
        viewModelScope.launch(Dispatchers.IO) {
            val brewerQueryFlow = queryFlow.combineTransform(typeFlow) { query, type ->
                if (type == SearchType.BREWER) emit(query)
            }

            brewerSearchRepository
                .getStream(brewerQueryFlow, queryLengthValidator, Accept(), SEARCH_DELAY)
                .distinctUntilChanged { old, new -> sameIds(old, new, Brewer::id) }
                .map(BrewerListModelAlphabeticMapper(brewerRepository, countryRepository)::map)
                .collect { _brewerResults.postValue(it) }
        }
    }

    private fun searchStyles() {
        viewModelScope.launch(Dispatchers.IO) {
            queryFlow
                .combineTransform(typeFlow) { query, type ->
                    if (type == SearchType.STYLE) emit(query)
                }
                .flatMapLatest { query ->
                    styleListRepository.getStream(Accept())
                        .map { filterStyles(it, query) }
                }
                .map(StateListMapper(::StyleListModel)::map)
                .collect { _styleResults.postValue(it) }
        }
    }

    private fun searchCountries() {
        viewModelScope.launch(Dispatchers.IO) {
            queryFlow
                .combineTransform(typeFlow) { query, type ->
                    if (type == SearchType.COUNTRY) emit(query)
                }
                .flatMapLatest { query ->
                    countryListRepository.getStream(Accept())
                        .map { filterCountries(it, query) }
                }
                .map(StateListMapper(::CountryListModel)::map)
                .collect { _countryResults.postValue(it) }
        }
    }

    private fun filterStyles(state: State<List<Style>>, query: String): State<List<Style>> {
        if (state !is State.Success) return state

        return state.value.filter { it.parent != null && it.parent > 0 }
            .filter { matcher(query, it.name) }
            .sortedBy(Style::name)
            .let { State.from(it) }
    }

    private fun filterCountries(state: State<List<Country>>, query: String): State<List<Country>> {
        if (state !is State.Success) return state

        return state.value.filter { matcher(query, it.name) }
            .sortedBy(Country::name)
            .let { State.from(it) }
    }

    private fun isBarcode(query: String): Boolean {
        // Just a simple sanity check before doing a barcode query
        return query.isDigitsOnly() && query.length > Constants.BARCODE_MIN_LENGTH
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

    @Suppress("MagicNumber")
    enum class SearchType(val value: Int) {
        BEER(0),
        BREWER(1),
        STYLE(2),
        COUNTRY(3);

        companion object {
            fun fromValue(value: Int): SearchType {
                return when (value) {
                    0 -> BEER
                    1 -> BREWER
                    2 -> STYLE
                    3 -> COUNTRY
                    else -> error("Invalid value")
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DELAY = 500L
    }
}
