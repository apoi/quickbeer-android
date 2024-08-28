package quickbeer.android.ui.adapter.place

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.PlaceListItemBinding
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory
import quickbeer.android.ui.adapter.base.ListViewHolder

class PlaceListTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.place_list_item
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return PlaceListViewHolder(createBinding(PlaceListItemBinding::inflate, parent))
    }
}
