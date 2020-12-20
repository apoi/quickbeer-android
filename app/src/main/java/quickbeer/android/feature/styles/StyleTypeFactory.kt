package quickbeer.android.feature.styles

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.SimpleListItemBinding
import quickbeer.android.ui.adapter.simple.ListItem
import quickbeer.android.ui.adapter.simple.ListTypeFactory
import quickbeer.android.ui.adapter.simple.ListViewHolder

class StyleTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.simple_list_item
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return StyleViewHolder(createBinding(SimpleListItemBinding::inflate, parent))
    }
}
