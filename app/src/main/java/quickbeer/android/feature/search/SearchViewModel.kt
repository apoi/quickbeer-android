package quickbeer.android.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.Constants
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.repository.Repository
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beerlist.repository.BeerSearchRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.brewerlist.repository.BrewerSearchRepository
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListModelRateCountMapper
import quickbeer.android.ui.adapter.brewer.BrewerListModel
import quickbeer.android.util.ktx.distinctUntilNewId
import quickbeer.android.util.ktx.mapState
import quickbeer.android.util.ktx.mapStateList
import quickbeer.android.util.ktx.normalize

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val beerSearchRepository: BeerSearchRepository,
    private val brewerRepository: BrewerRepository,
    private val brewerSearchRepository: BrewerSearchRepository,
    private val countryRepository: CountryRepository,
    private val beerListMapper: BeerListModelRateCountMapper
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val typeFlow = MutableStateFlow(SearchType.BEER)

    private val _beerResults = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val beerResults: StateFlow<State<List<BeerListModel>>> = _beerResults

    private val _brewerResults = MutableStateFlow<State<List<BrewerListModel>>>(State.Initial)
    val brewerResults: StateFlow<State<List<BrewerListModel>>> = _brewerResults

    private val queryLengthValidator = object : Repository.KeyValidator<String> {
        override fun isEmpty(key: String) = key.isEmpty()
        override fun isValid(key: String) = key.length >= Constants.QUERY_MIN_LENGTH
    }

    init {
        searchBeers()
        searchBrewers()
    }

    fun onSearchQueryChanged(query: String) {
        queryFlow.value = query.normalize().orEmpty()
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
                // Avoid re-sorting the results if the set of beers didn't change
                .distinctUntilNewId(Beer::id)
                .map(beerListMapper::map)
                .collectLatest(_beerResults::emit)
        }
    }

    private fun searchBrewers() {
        viewModelScope.launch(Dispatchers.IO) {
            val brewerQueryFlow = queryFlow.combineTransform(typeFlow) { query, type ->
                if (type == SearchType.BREWER) emit(query)
            }

            brewerSearchRepository
                .getStream(brewerQueryFlow, queryLengthValidator, Accept(), SEARCH_DELAY)
                .distinctUntilNewId(Brewer::id)
                .mapState { it.sortedWith(compareBy(Brewer::name, Brewer::id)) }
                .mapStateList {
                    BrewerListModel(it.id, it.countryId, brewerRepository, countryRepository)
                }
                .collectLatest(_brewerResults::emit)
        }
    }

    @Suppress("MagicNumber")
    enum class SearchType(val value: Int) {
        BEER(0),
        BREWER(1);

        companion object {
            fun fromValue(value: Int): SearchType {
                return when (value) {
                    0 -> BEER
                    1 -> BREWER
                    else -> error("Invalid value")
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DELAY = 500L
    }
}
