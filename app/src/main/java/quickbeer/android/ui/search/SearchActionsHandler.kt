package quickbeer.android.ui.search

import kotlinx.coroutines.flow.Flow
import quickbeer.android.ui.adapter.suggestion.SuggestionListModel

interface SearchActionsHandler {

    val suggestions: Flow<List<SuggestionListModel>>

    fun onSearchChanged(query: String)
}
