package quickbeer.android.ui.adapter.search

data class SearchResult(
    val id: Int,
    val type: Type,
    val text: String
) {
    enum class Type {
        BEER, BREWERY, SEARCH
    }
}
