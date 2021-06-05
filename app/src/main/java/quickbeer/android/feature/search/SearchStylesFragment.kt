package quickbeer.android.feature.search

import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.navigation.Destination
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.style.StyleListModel
import quickbeer.android.ui.adapter.style.StyleTypeFactory
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType

@AndroidEntryPoint
class SearchStylesFragment : SearchTabFragment<StyleListModel>() {

    override val resultAdapter = ListAdapter<StyleListModel>(StyleTypeFactory())
    override val resultPoolType = PoolType.STYLE_LIST
    override fun resultFlow() = viewModel.styleResults

    override fun onItemSelected(item: StyleListModel) {
        navigate(Destination.Style(item.style.id))
    }
}
