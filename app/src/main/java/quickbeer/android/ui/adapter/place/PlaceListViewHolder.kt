package quickbeer.android.ui.adapter.place

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quickbeer.android.data.state.State
import quickbeer.android.databinding.ListItemTwoRowsBinding
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.place.Place
import quickbeer.android.ui.adapter.base.ScopeListViewHolder

class PlaceListViewHolder(
    private val binding: ListItemTwoRowsBinding
) : ScopeListViewHolder<PlaceListModel>(binding.root) {

    override fun bind(item: PlaceListModel, scope: CoroutineScope) {
        clear()

        scope.launch {
            item.getPlace(item.placeId).collect { state ->
                if (state is State.Loading && state.value?.countryId != null) {
                    getCountry(state.value, item, scope)
                } else if (state is State.Success && state.value.countryId != null) {
                    getCountry(state.value, item, scope)
                }

                state.valueOrNull()?.let {
                    withContext(Dispatchers.Main) { setPlace(it) }
                }
            }
        }
    }

    private fun setPlace(place: Place) {
        binding.infoPrimary.text = place.name
    }

    private fun getCountry(place: Place?, item: PlaceListModel, scope: CoroutineScope) {
        if (place?.countryId == null) return

        scope.launch {
            item.getCountry(place.countryId)
                .map { it.valueOrNull() }
                .collect { withContext(Dispatchers.Main) { setAddress(it) } }
        }
    }

    private fun setAddress(country: Country?) {
        if (country == null) return

        binding.icon.text = country.code
        binding.infoSecondary.text = country.name
    }

    private fun clear() {
        binding.infoPrimary.text = ""
        binding.infoSecondary.text = ""
    }
}
