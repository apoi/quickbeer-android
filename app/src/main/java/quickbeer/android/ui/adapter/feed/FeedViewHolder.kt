package quickbeer.android.ui.adapter.feed

import quickbeer.android.databinding.SimpleListItemBinding
import quickbeer.android.ui.adapter.base.ListViewHolder

class FeedViewHolder(
    private val binding: SimpleListItemBinding
) : ListViewHolder<FeedListModel>(binding.root) {

    override fun bind(item: FeedListModel) {
        binding.itemIcon.text = item.feedItem.type?.id?.toString()
        binding.itemName.text = item.feedItem.type?.name
    }
}
