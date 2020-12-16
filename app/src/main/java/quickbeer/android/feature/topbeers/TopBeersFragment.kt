package quickbeer.android.feature.topbeers

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.BeerListFragmentBinding
import quickbeer.android.feature.search.SearchViewModel
import quickbeer.android.feature.shared.adapter.BeerListModel
import quickbeer.android.feature.shared.adapter.BeerListTypeFactory
import quickbeer.android.feature.topbeers.TopBeersViewEffect.Search
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.simple.ListAdapter
import quickbeer.android.ui.listener.setClickListener
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType
import quickbeer.android.ui.search.SearchBarFragment
import quickbeer.android.ui.search.SearchActionsHandler
import quickbeer.android.ui.searchview.widget.SearchView
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

class TopBeersFragment : SearchBarFragment(R.layout.beer_list_fragment) {

    private val binding by viewBinding(BeerListFragmentBinding::bind)
    private val viewModel by viewModel<TopBeersViewModel>()
    private val searchViewModel by viewModel<SearchViewModel> { parametersOf(null) }
    private var beersAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())

    override val searchHint = R.string.search_hint
    override fun rootLayout() = binding.layout
    override fun topInsetView() = binding.layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
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
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    override fun observeViewState() {
        observe(viewModel.viewState) { state ->
            when (state) {
                State.Loading -> Unit
                State.Empty -> Unit
                is State.Success -> beersAdapter.setItems(state.value)
                is State.Error -> Unit
            }
        }

        observe(viewModel.viewEffect) { effect ->
            when (effect) {
                is Search -> {
                    navigate(TopBeersFragmentDirections.toSearch(effect.query))
                }
            }
        }
    }

    private fun onBeerSelected(beer: BeerListModel) {
        navigate(TopBeersFragmentDirections.toDetails(beer.id))
    }

    override fun searchView(): SearchView {
        return binding.searchView
    }

    override fun searchActionsHandler(): SearchActionsHandler {
        return searchViewModel
    }
}
