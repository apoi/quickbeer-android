package quickbeer.android.ui.adapter.brewer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import quickbeer.android.databinding.BrewerListItemBinding
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.country.Country
import quickbeer.android.feature.beerdetails.model.Address
import quickbeer.android.ui.adapter.simple.ScopeListViewHolder

class BrewerListViewHolder(
    private val binding: BrewerListItemBinding
) : ScopeListViewHolder<BrewerListModel>(binding.root) {

    override fun bind(item: BrewerListModel, scope: CoroutineScope) {
        clear()
        scope.launch {
            setData(item.getBrewer(item.brewerId), item.getCountry(item.countryId))
        }
    }

    private fun setData(brewer: Brewer?, country: Country?) {
        if (brewer == null || country == null) return

        val address = Address.from(brewer, country)
        binding.brewerName.text = brewer.name
        binding.brewerLocation.text = address.cityAndCountry()
        binding.brewerCountry.text = country.code
    }

    private fun clear() {
        binding.brewerName.text = ""
        binding.brewerLocation.text = ""
    }
}
