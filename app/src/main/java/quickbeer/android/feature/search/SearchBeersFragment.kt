package quickbeer.android.feature.search

import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.navigation.Destination
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListTypeFactory
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType

@AndroidEntryPoint
class SearchBeersFragment : SearchTabFragment<BeerListModel>() {

    override val resultAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())
    override val resultPoolType = PoolType.BEER_LIST
    override fun resultFlow() = viewModel.beerResults

    override fun onItemSelected(item: BeerListModel) {
        navigate(Destination.Beer(item.beerId))
    }
}
