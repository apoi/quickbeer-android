package quickbeer.android.ui.adapter.discover

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import quickbeer.android.R
import quickbeer.android.databinding.DiscoverListItemBinding
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory
import quickbeer.android.ui.adapter.base.ListViewHolder

class DiscoverTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.discover_list_item
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return DiscoverViewHolder(createBinding(DiscoverListItemBinding::inflate, parent))
    }

    override fun createPool(): RecyclerView.RecycledViewPool {
        return RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(R.layout.discover_list_item, DEFAULT_POOL_SIZE)
        }
    }
}
