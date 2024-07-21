package quickbeer.android.ui.adapter.feed

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory
import quickbeer.android.ui.adapter.base.ListViewHolder

class FeedTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.feed_list_item
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return FeedViewHolder(createBinding(FeedListItemBinding::inflate, parent))
    }
}
