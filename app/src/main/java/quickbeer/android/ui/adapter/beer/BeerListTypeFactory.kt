package quickbeer.android.ui.adapter.beer

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import quickbeer.android.R
import quickbeer.android.databinding.BeerListItemBinding
import quickbeer.android.ui.adapter.simple.ListItem
import quickbeer.android.ui.adapter.simple.ListTypeFactory
import quickbeer.android.ui.adapter.simple.ListViewHolder

class BeerListTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.beer_list_item
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return BeerListViewHolder(createBinding(BeerListItemBinding::inflate, parent))
    }

    override fun createPool(): RecyclerView.RecycledViewPool {
        return RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(R.layout.beer_list_item, DEFAULT_POOL_SIZE)
        }
    }
}
