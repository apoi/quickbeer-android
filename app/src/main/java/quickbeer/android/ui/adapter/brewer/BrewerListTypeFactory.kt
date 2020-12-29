package quickbeer.android.ui.adapter.brewer

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.BrewerListItemBinding
import quickbeer.android.ui.adapter.simple.ListItem
import quickbeer.android.ui.adapter.simple.ListTypeFactory
import quickbeer.android.ui.adapter.simple.ListViewHolder

class BrewerListTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.brewer_list_item
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return BrewerListViewHolder(createBinding(BrewerListItemBinding::inflate, parent))
    }
}
