package quickbeer.android.ui.adapter.brewer

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.ListItemTwoRowsBinding
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory
import quickbeer.android.ui.adapter.base.ListViewHolder

class BrewerListTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.brewer_list_item
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return BrewerListViewHolder(createBinding(ListItemTwoRowsBinding::inflate, parent))
    }
}
