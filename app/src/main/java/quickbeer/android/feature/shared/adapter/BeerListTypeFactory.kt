package quickbeer.android.feature.shared.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import quickbeer.android.R
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

    override fun createPool(): RecyclerView.RecycledViewPool {
        return RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(R.layout.beer_list_item, DEFAULT_POOL_SIZE)
        }
    }
}
