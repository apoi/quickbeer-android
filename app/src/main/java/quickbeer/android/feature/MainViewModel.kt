package quickbeer.android.feature

import android.app.Activity
import androidx.lifecycle.ViewModel
import quickbeer.android.ui.adapter.search.SearchAdapter
import quickbeer.android.ui.adapter.search.SearchResult
import timber.log.Timber

class MainViewModel : ViewModel() {

    private val results: MutableList<SearchResult> = mutableListOf()

    private var searchAdapter: SearchAdapter? = null

    fun getSearchAdapter(activity: Activity): SearchAdapter {
        return SearchAdapter(activity).also {
            searchAdapter = it
        }
    }

    fun onSearchChanged(value: String) {
        Timber.w("Query: $value")
        results.add(SearchResult(0, SearchResult.Type.SEARCH, value))
        searchAdapter?.setItems(results)
    }

    fun onSearchSubmit(value: String) {
        Timber.w("Submit: $value")
        results.clear()
        searchAdapter?.setItems(results)
    }

    fun onSuggestionClicked(position: Int) {
        Timber.w("Suggestion: $position")
    }
}
