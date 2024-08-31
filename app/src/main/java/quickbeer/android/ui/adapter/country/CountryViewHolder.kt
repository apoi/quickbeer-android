package quickbeer.android.ui.adapter.country

import quickbeer.android.databinding.ListItemOneRowBinding
import quickbeer.android.ui.adapter.base.ListViewHolder

class CountryViewHolder(
    private val binding: ListItemOneRowBinding
) : ListViewHolder<CountryListModel>(binding.root) {

    override fun bind(item: CountryListModel) {
        binding.itemIcon.text = item.country.code
        binding.itemName.text = item.country.name
    }
}
