package quickbeer.android.ui.adapter.brewer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quickbeer.android.data.state.State
import quickbeer.android.databinding.BrewerListItemBinding
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.country.Country
import quickbeer.android.feature.beerdetails.model.Address
import quickbeer.android.ui.adapter.base.ScopeListViewHolder

class BrewerListViewHolder(
    private val binding: BrewerListItemBinding
) : ScopeListViewHolder<BrewerListModel>(binding.root) {

    override fun bind(item: BrewerListModel, scope: CoroutineScope) {
        clear()

        scope.launch {
            item.getBrewer(item.brewerId).collect {
                if (it is State.Loading && it.value?.countryId != null) {
                    getCountry(it.value, item, scope)
                } else if (it is State.Success && it.value.countryId != null) {
                    getCountry(it.value, item, scope)
                }

                withContext(Dispatchers.Main) { updateState(it) }
            }
        }
    }

    private fun getCountry(brewer: Brewer?, item: BrewerListModel, scope: CoroutineScope) {
        if (brewer?.countryId == null) return

        scope.launch {
            item.getCountry(brewer.countryId)
                .map { mergeAddress(brewer, it) }
                .collect { withContext(Dispatchers.Main) { setAddress(it) } }
        }
    }

    private fun mergeAddress(brewer: Brewer, country: State<Country>): Address? {
        return if (country is State.Success) {
            Address.from(brewer, country.value)
        } else null
    }

    private fun updateState(state: State<Brewer>) {
        when (state) {
            is State.Loading -> state.value?.let(::setBrewer)
            is State.Success -> setBrewer(state.value)
        }
    }

    private fun setBrewer(brewer: Brewer) {
        binding.brewerName.text = brewer.name
    }

    private fun setAddress(address: Address?) {
        if (address == null) return

        binding.brewerLocation.text = address.cityAndCountry()
        binding.brewerCountry.text = address.code
    }

    private fun clear() {
        binding.brewerName.text = ""
        binding.brewerLocation.text = ""
    }
}
