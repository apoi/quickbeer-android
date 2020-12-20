package quickbeer.android.feature.styles

import quickbeer.android.databinding.SimpleListItemBinding
import quickbeer.android.ui.adapter.simple.ListViewHolder

class StyleViewHolder(
    private val binding: SimpleListItemBinding
) : ListViewHolder<StyleItem>(binding.root) {

    override fun bind(item: StyleItem) {
        binding.itemIcon.text = item.style.code
        binding.itemName.text = item.style.name
    }
}
