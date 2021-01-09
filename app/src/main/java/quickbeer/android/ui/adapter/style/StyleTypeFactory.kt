package quickbeer.android.ui.adapter.style

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.SimpleListItemBinding
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory
import quickbeer.android.ui.adapter.base.ListViewHolder

class StyleTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.simple_list_item
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return StyleViewHolder(createBinding(SimpleListItemBinding::inflate, parent))
    }
}
