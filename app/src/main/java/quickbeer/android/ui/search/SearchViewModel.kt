package quickbeer.android.ui.search

import quickbeer.android.ui.adapter.search.SearchResult
import quickbeer.android.ui.adapter.simple.ListAdapter

interface SearchViewModel {

    fun getSearchAdapter(): ListAdapter<SearchResult>

    fun onSearchChanged(value: String)

    fun onSearchSubmit(value: String)

    fun onSuggestionClicked(position: Int)

    fun getSuggestionText(position: Int): String
}
