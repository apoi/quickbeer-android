package quickbeer.android.ui.adapter.search

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.BeerListItemBinding
import quickbeer.android.databinding.SearchViewSuggestionBinding
import quickbeer.android.feature.shared.adapter.BeerListModel
import quickbeer.android.feature.shared.adapter.BeerListViewHolder
import quickbeer.android.ui.adapter.simple.ListItem
import quickbeer.android.ui.adapter.simple.ListTypeFactory
import quickbeer.android.ui.adapter.simple.ListViewHolder

class SearchResultTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return when (item) {
            is SearchResult -> R.layout.search_view_suggestion
            else -> error("Invalid item")
        }
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return when (type) {
            R.layout.search_view_suggestion -> {
                SuggestionViewHolder(createBinding(SearchViewSuggestionBinding::inflate, parent))
            }
            else -> error("Invalid type")
        }
    }
}
