package quickbeer.android.ui.search

import quickbeer.android.ui.adapter.search.SearchResult
import quickbeer.android.ui.adapter.simple.ListAdapter

interface SearchBarInterface {

    fun getSearchAdapter(): ListAdapter<SearchResult>

    fun onSearchChanged(query: String)

    fun onSearchSubmit(query: String)

    fun onSuggestionClicked(position: Int)

    fun getSuggestionText(position: Int): String
}
