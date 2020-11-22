package quickbeer.android.ui.search

import android.content.Context
import quickbeer.android.ui.adapter.search.SearchAdapter

interface SearchViewModel {

    fun getSearchAdapter(context: Context): SearchAdapter

    fun onSearchChanged(value: String)

    fun onSearchSubmit(value: String)

    fun onSuggestionClicked(position: Int)

    fun getSuggestionText(position: Int): String
}
