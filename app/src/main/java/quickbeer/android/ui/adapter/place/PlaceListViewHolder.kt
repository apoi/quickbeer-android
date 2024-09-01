package quickbeer.android.ui.adapter.place

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quickbeer.android.databinding.ListItemTwoRowsBinding
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.place.Place
import quickbeer.android.ui.adapter.base.ScopeListViewHolder
import quickbeer.android.util.ktx.combineDistinctValues

class PlaceListViewHolder(
    private val binding: ListItemTwoRowsBinding
) : ScopeListViewHolder<PlaceListModel>(binding.root) {

    override fun bind(item: PlaceListModel, scope: CoroutineScope) {
        clear()

        scope.launch {
            item.getPlace(item.placeId)
                .combineDistinctValues(
                    { it.countryId?.let(item::getCountry) ?: emptyFlow() },
                    { place, country -> Pair(place, country) }
                )
                .collect { (place, country) ->
                    withContext(Dispatchers.Main) {
                        setPlace(place, country)
                    }
                }
        }
    }

    private fun setPlace(place: Place, country: Country?) {
        binding.icon.text = country?.code.orEmpty()
        binding.infoPrimary.text = place.name
        binding.infoSecondary.text = createAddress(place, country)
    }

    private fun createAddress(place: Place, country: Country?): String {
        return listOfNotNull(place.city, country?.name)
            .joinToString(", ")
            .ifEmpty { "Unknown" }
    }

    private fun clear() {
        binding.icon.text = ""
        binding.infoPrimary.text = ""
        binding.infoSecondary.text = ""
    }
}
