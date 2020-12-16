package quickbeer.android.ui.search

interface SearchActionsHandler {

    fun onSearchChanged(query: String)

    fun onSearchSubmit(query: String)

    fun onSuggestionClicked(position: Int)
}
