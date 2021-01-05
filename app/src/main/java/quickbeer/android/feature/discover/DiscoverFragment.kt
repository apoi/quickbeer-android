package quickbeer.android.feature.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.progressindicator.CircularProgressIndicator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.BeerListStandaloneFragmentBinding
import quickbeer.android.databinding.DiscoverTabTitleBinding
import quickbeer.android.navigation.Destination
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

class DiscoverFragment : SearchBarFragment(R.layout.beer_list_standalone_fragment) {

    private val binding by viewBinding(BeerListStandaloneFragmentBinding::bind)
    private val viewModel by viewModel<DiscoverViewModel>()
    private val searchViewModel by sharedViewModel<SearchViewModel>()
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewPager.adapter = SearchPagerAdapter(childFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        // Set custom tab layouts to get progress indicators
        (0 until binding.tabLayout.tabCount).forEach { index ->
            val tabBinding = DiscoverTabTitleBinding.inflate(LayoutInflater.from(context))
            tabBinding.title.text = when (index) {
                0 -> getString(R.string.search_tab_beers)
                1 -> getString(R.string.search_tab_brewers)
                else -> getString(R.string.search_tab_styles)
            }
            binding.tabLayout.getTabAt(index)?.customView = tabBinding.layout
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    override fun observeViewState() {
        observe(viewModel.viewState) { state ->
            when (state) {
                is State.Loading -> {
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

        observe(searchViewModel.beerResults) { state ->
            updateSearchTabProgress(0, state is State.Loading)
        }

        observe(searchViewModel.brewerResults) { state ->
            updateSearchTabProgress(1, state is State.Loading)
        }

        observe(searchViewModel.styleResults) { state ->
            updateSearchTabProgress(2, state is State.Loading)
        }
    }

    private fun updateSearchTabProgress(tab: Int, inProgress: Boolean) {
        binding.tabLayout.getTabAt(tab)
            ?.customView
            ?.findViewById<CircularProgressIndicator>(R.id.progress)
            ?.isVisible = inProgress
    }

    override fun searchView(): SearchView {
        return binding.searchView
    }

    override fun searchActions(): SearchActionsHandler {
        return searchViewModel
    }

    override fun onSearchFocusChanged(hasFocus: Boolean) {
        super.onSearchFocusChanged(hasFocus)

        binding.mainContent.isVisible = !hasFocus
        binding.tabLayout.isVisible = hasFocus
        binding.viewPager.isVisible = hasFocus
    }

    override fun onSearchQuerySubmit(query: String) {
        searchViewModel.onSearchChanged(query)
    }

    private fun onBeerSelected(beer: BeerListModel) {
        navigate(Destination.Beer(beer.id))
    }
}
