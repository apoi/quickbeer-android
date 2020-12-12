package quickbeer.android.feature.search

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.BeerListFragmentBinding
import quickbeer.android.feature.shared.adapter.BeerListModel
import quickbeer.android.feature.shared.adapter.BeerListTypeFactory
import quickbeer.android.feature.topbeers.TopBeersFragmentDirections
import quickbeer.android.feature.topbeers.TopBeersViewEffect
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.simple.ListAdapter
import quickbeer.android.ui.listener.setClickListener
import quickbeer.android.ui.search.SearchBarFragment
import quickbeer.android.ui.search.SearchBarInterface
import quickbeer.android.ui.searchview.widget.SearchView
import quickbeer.android.ui.searchview.widget.SearchView.NavigationMode
import quickbeer.android.util.ktx.viewBinding

class SearchFragment : SearchBarFragment(R.layout.beer_list_fragment) {

    override fun rootLayout() = binding.layout
    override fun topInsetView() = binding.layout

    private val binding by viewBinding(BeerListFragmentBinding::bind)
    private val beersAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())

    private val args: SearchFragmentArgs by navArgs()
    private val viewModel by viewModel<SearchViewModel> { parametersOf(args.query) }

    override val searchHint = R.string.search_hint

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.apply {
            query = args.query
            navigationMode = NavigationMode.BACK
        }

        binding.recyclerView.apply {
            adapter = beersAdapter
            layoutManager = LinearLayoutManager(context)

            setHasFixedSize(true)
            addItemDecoration(DividerDecoration(context))
            setClickListener(::onBeerSelected)
        }
    }

    override fun observeViewState() {
        observe(viewModel.viewState) { state ->
            when (state) {
                State.Loading -> {
                    beersAdapter.setItems(emptyList())
                    binding.progress.show()
                }
                State.Empty -> {
                    beersAdapter.setItems(emptyList())
                    binding.progress.hide()
                }
                is State.Success -> {
                    beersAdapter.setItems(state.value)
                    binding.progress.hide()
                }
                is State.Error -> {
                    beersAdapter.setItems(emptyList())
                    binding.progress.hide()
                }
            }
        }

        observe(viewModel.viewEffect) { effect ->
            when (effect) {
                is TopBeersViewEffect.Search -> {
                    navigate(TopBeersFragmentDirections.toSearch(effect.query))
                }
            }
        }
    }

    private fun onBeerSelected(beer: BeerListModel) {
        navigate(SearchFragmentDirections.toDetails(beer.id))
    }

    override fun searchView(): SearchView {
        return binding.searchView
    }

    override fun searchViewModel(): SearchBarInterface {
        return viewModel
    }
}
