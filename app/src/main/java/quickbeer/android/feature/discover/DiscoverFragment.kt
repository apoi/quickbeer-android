package quickbeer.android.feature.discover

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.DiscoverFragmentBinding
import quickbeer.android.databinding.ListContentBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.navigation.Destination
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListTypeFactory
import quickbeer.android.ui.base.Resetable
import quickbeer.android.ui.listener.setClickListener
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType
import quickbeer.android.ui.search.SearchBarFragment
import quickbeer.android.ui.view.SearchView
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

class DiscoverFragment : SearchBarFragment(R.layout.discover_fragment), Resetable {

    private val binding by viewBinding(DiscoverFragmentBinding::bind)
    private val listBinding by viewBinding(ListContentBinding::bind)

    private val viewModel by viewModel<DiscoverViewModel>()
    private val beersAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())

    override val searchHint = R.string.search_hint

    override fun topInsetView() = binding.layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listBinding.message.text = getString(R.string.message_start)

        listBinding.recyclerView.apply {
            adapter = beersAdapter
            layoutManager = LinearLayoutManager(context).apply {
                recycleChildrenOnDetach = true
            }

            setHasFixedSize(true)
            addItemDecoration(DividerDecoration(context))
            setClickListener(::onBeerSelected)

            setRecycledViewPool(
                (activity as RecycledPoolHolder)
                    .getPool(PoolType.BEER_LIST, beersAdapter::createPool)
            )
        }
    }

    override fun onDestroyView() {
        listBinding.recyclerView.adapter = null
        super.onDestroyView()
    }

    override fun observeViewState() {
        observe(viewModel.viewState) { state ->
            when (state) {
                is State.Loading -> {
                    beersAdapter.setItems(emptyList())
                    listBinding.message.isVisible = false
                    listBinding.progress.show()
                }
                State.Empty -> {
                    beersAdapter.setItems(emptyList())
                    listBinding.message.text = getString(R.string.message_empty)
                    listBinding.message.isVisible = true
                    listBinding.progress.hide()
                }
                is State.Success -> {
                    beersAdapter.setItems(state.value)
                    listBinding.message.isVisible = false
                    listBinding.progress.hide()
                }
                is State.Error -> {
                    beersAdapter.setItems(emptyList())
                    listBinding.message.text = getString(R.string.message_error)
                    listBinding.message.isVisible = true
                    listBinding.progress.hide()
                }
            }
        }
    }

    override fun searchView(): SearchView {
        return binding.searchView
    }

    override fun onSearchFocusChanged(hasFocus: Boolean) {
        super.onSearchFocusChanged(hasFocus)

        if (hasFocus) navigate(DiscoverFragmentDirections.toSearch(), NavAnim.NONE)
    }

    override fun onReset() {
        listBinding.recyclerView.smoothScrollToPosition(0)
    }

    private fun onBeerSelected(beer: BeerListModel) {
        navigate(Destination.Beer(beer.id))
    }

    override fun onBarcodeScanResult(barcode: String, beers: List<Beer>) {
        if (beers.size == 1) {
            navigate(Destination.Beer(beers.first().id))
        } else {
            navigate(DiscoverFragmentDirections.toSearch(barcode = barcode), NavAnim.NONE)
        }
    }
}
