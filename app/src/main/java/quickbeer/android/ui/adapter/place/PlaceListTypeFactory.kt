package quickbeer.android.ui.adapter.place

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.ListItemTwoRowsBinding
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory
import quickbeer.android.ui.adapter.base.ListViewHolder

class PlaceListTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.list_item_one_row
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return PlaceListViewHolder(createBinding(ListItemTwoRowsBinding::inflate, parent))
    }
}
