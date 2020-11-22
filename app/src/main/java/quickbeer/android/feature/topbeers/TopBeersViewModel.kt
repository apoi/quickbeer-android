package quickbeer.android.feature.topbeers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beersearch.repository.TopBeersRepository
import quickbeer.android.feature.shared.adapter.BeerListModel
import quickbeer.android.ui.adapter.search.SearchAdapter
import quickbeer.android.ui.adapter.search.SearchResult
import quickbeer.android.ui.search.SearchViewModel
import timber.log.Timber

class TopBeersViewModel(
    private val repository: TopBeersRepository,
    private val beerStore: BeerRepository
) : ViewModel(), SearchViewModel {

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

    private val results: MutableList<SearchResult> = mutableListOf()

    private var searchAdapter: SearchAdapter? = null

    override fun getSearchAdapter(context: Context): SearchAdapter {
        return SearchAdapter(context).also {
            searchAdapter = it
        }
    }

    override fun onSearchChanged(value: String) {
        Timber.w("Query: $value")

        if (value.isNotEmpty()) {
            results.add(SearchResult(0, SearchResult.Type.SEARCH, value))
            searchAdapter?.setItems(results)
        }
    }

    override fun onSearchSubmit(value: String) {
        Timber.w("Submit: $value")
        results.clear()
        searchAdapter?.setItems(results)
    }

    override fun onSuggestionClicked(position: Int) {
        Timber.w("Suggestion: $position")
    }

    override fun getSuggestionText(position: Int): String {
        Timber.w("Get suggestion: $position")

        return searchAdapter?.getSuggestionText(position).orEmpty()
    }
}
