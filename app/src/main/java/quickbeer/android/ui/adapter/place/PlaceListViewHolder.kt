package quickbeer.android.ui.adapter.place

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quickbeer.android.databinding.ListItemTwoRowsBinding
import quickbeer.android.domain.place.Place
import quickbeer.android.ui.adapter.base.ScopeListViewHolder

class PlaceListViewHolder(
    private val binding: ListItemTwoRowsBinding
) : ScopeListViewHolder<PlaceListModel>(binding.root) {

    override fun bind(item: PlaceListModel, scope: CoroutineScope) {
        clear()

        scope.launch {
            item.getPlace(item.placeId).collect { state ->
                state.valueOrNull()?.let {
                    withContext(Dispatchers.Main) { setPlace(it) }
                }
            }
        }
    }

    private fun setPlace(place: Place) {
        binding.infoPrimary.text = place.name
    }

    private fun clear() {
        binding.infoPrimary.text = ""
        binding.infoSecondary.text = ""
    }
}
