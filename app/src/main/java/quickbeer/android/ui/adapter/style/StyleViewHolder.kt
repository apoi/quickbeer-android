package quickbeer.android.ui.adapter.style

import quickbeer.android.databinding.SimpleListItemBinding
import quickbeer.android.ui.adapter.simple.ListViewHolder

class StyleViewHolder(
    private val binding: SimpleListItemBinding
) : ListViewHolder<StyleListModel>(binding.root) {

    override fun bind(item: StyleListModel) {
        binding.itemIcon.text = item.style.code
        binding.itemName.text = item.style.name
    }
}
