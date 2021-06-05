package quickbeer.android.feature.search

import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.navigation.Destination
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.brewer.BrewerListModel
import quickbeer.android.ui.adapter.brewer.BrewerListTypeFactory
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType

@AndroidEntryPoint
class SearchBrewersFragment : SearchTabFragment<BrewerListModel>() {

    override val resultAdapter = ListAdapter<BrewerListModel>(BrewerListTypeFactory())
    override val resultPoolType = PoolType.BREWER_LIST
    override fun resultFlow() = viewModel.brewerResults

    override fun onItemSelected(item: BrewerListModel) {
        navigate(Destination.Brewer(item.brewerId))
    }
}
