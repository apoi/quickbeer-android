package quickbeer.android.ui.adapter.discover

import quickbeer.android.databinding.DiscoverListItemBinding
import quickbeer.android.ui.adapter.base.ListViewHolder

class DiscoverViewHolder(
    private val binding: DiscoverListItemBinding
) : ListViewHolder<DiscoverListModel>(binding.root) {

    override fun bind(item: DiscoverListModel) {
        binding.item.icon.setImageResource(item.icon)
        binding.item.label.setText(item.title)
        binding.item.position = item.position
    }
}
