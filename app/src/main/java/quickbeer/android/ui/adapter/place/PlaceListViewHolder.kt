package quickbeer.android.ui.adapter.place

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quickbeer.android.databinding.PlaceListItemBinding
import quickbeer.android.domain.place.Place
import quickbeer.android.ui.adapter.base.ScopeListViewHolder

class PlaceListViewHolder(
    private val binding: PlaceListItemBinding
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
        binding.placeName.text = place.name
    }

    private fun clear() {
        binding.placeName.text = ""
        binding.placeSomething.text = ""
    }
}
