package quickbeer.android.ui.search

import kotlinx.coroutines.flow.Flow
import quickbeer.android.ui.adapter.search.SearchSuggestion

interface SearchActionsHandler {

    val suggestions: Flow<List<SearchSuggestion>>

    fun onSearchChanged(query: String)
}
