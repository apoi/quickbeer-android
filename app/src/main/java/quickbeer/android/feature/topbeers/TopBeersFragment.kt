package quickbeer.android.feature.topbeers

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.BeerListStandaloneFragmentBinding
import quickbeer.android.feature.search.SearchViewModel
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListTypeFactory
import quickbeer.android.ui.adapter.simple.ListAdapter
import quickbeer.android.ui.listener.setClickListener
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType
import quickbeer.android.ui.search.SearchActionsHandler
import quickbeer.android.ui.search.SearchBarFragment
import quickbeer.android.ui.searchview.widget.SearchView
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

class TopBeersFragment : SearchBarFragment(R.layout.beer_list_standalone_fragment) {

    private val binding by viewBinding(BeerListStandaloneFragmentBinding::bind)
    private val viewModel by viewModel<TopBeersViewModel>()
    private val searchViewModel by viewModel<SearchViewModel> { parametersOf(null) }
    private val beersAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())

    override val searchHint = R.string.search_hint
    override fun rootLayout() = binding.layout
    override fun topInsetView() = binding.layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.message.text = getString(R.string.message_start)

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
                State.Loading -> {
                    beersAdapter.setItems(emptyList())
                    binding.message.isVisible = false
                    binding.progress.show()
                }
                State.Empty -> {
                    beersAdapter.setItems(emptyList())
                    binding.message.text = getString(R.string.message_empty)
                    binding.message.isVisible = true
                    binding.progress.hide()
                }
                is State.Success -> {
                    beersAdapter.setItems(state.value)
                    binding.message.isVisible = false
                    binding.progress.hide()
                }
                is State.Error -> {
                    beersAdapter.setItems(emptyList())
                    binding.message.text = getString(R.string.message_error)
                    binding.message.isVisible = true
                    binding.progress.hide()
                }
            }
        }
    }

    override fun searchView(): SearchView {
        return binding.searchView
    }

    override fun searchActions(): SearchActionsHandler {
        return searchViewModel
    }

    private fun onBeerSelected(beer: BeerListModel) {
        navigate(TopBeersFragmentDirections.toBeer(beer.id))
    }

    override fun onSearchQuerySubmit(query: String) {
        navigate(TopBeersFragmentDirections.toSearch(query))
    }
}
