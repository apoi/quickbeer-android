package quickbeer.android.feature.shared.adapter

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.AlbumListItemBinding
import quickbeer.android.databinding.BeerListItemBinding
import quickbeer.android.ui.adapter.simple.ListItem
import quickbeer.android.ui.adapter.simple.ListTypeFactory
import quickbeer.android.ui.adapter.simple.ListViewHolder

class BeerListTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return when (item) {
            is BeerListModel -> R.layout.beer_list_item
            else -> error("Invalid item")
        }
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return when (type) {
            R.layout.beer_list_item -> {
                BeerListViewHolder(createBinding(BeerListItemBinding::inflate, parent))
            }
            else -> error("Invalid type")
        }
    }
}
