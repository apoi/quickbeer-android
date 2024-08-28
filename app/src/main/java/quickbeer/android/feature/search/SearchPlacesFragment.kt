package quickbeer.android.feature.search

import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.navigation.Destination
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.place.PlaceListModel
import quickbeer.android.ui.adapter.place.PlaceListTypeFactory
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType

@AndroidEntryPoint
class SearchPlacesFragment : SearchTabFragment<PlaceListModel>() {

    override val resultAdapter = ListAdapter<PlaceListModel>(PlaceListTypeFactory())
    override val resultPoolType = PoolType.PLACE_LIST
    override fun resultFlow() = viewModel.placeResults

    override fun onItemSelected(item: PlaceListModel) {
        navigate(Destination.Place(item.placeId))
    }
}
