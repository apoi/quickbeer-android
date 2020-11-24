package quickbeer.android.ui.adapter.search

import quickbeer.android.ui.adapter.simple.ListItem
import quickbeer.android.ui.adapter.simple.ListTypeFactory

data class SearchResult(
    val id: Int,
    val type: Type,
    val text: String
) : ListItem {

    override fun id() = id.toLong()

    override fun type(factory: ListTypeFactory) = factory.type(this)

    enum class Type {
        BEER, BREWERY, SEARCH
    }
}
