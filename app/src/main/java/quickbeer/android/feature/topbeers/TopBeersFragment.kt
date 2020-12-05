package quickbeer.android.feature.topbeers

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.BeerListFragmentBinding
import quickbeer.android.feature.shared.adapter.BeerListModel
import quickbeer.android.feature.shared.adapter.BeerListTypeFactory
import quickbeer.android.feature.topbeers.TopBeersViewEffect.Search
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.simple.ListAdapter
import quickbeer.android.ui.search.SearchBarFragment
import quickbeer.android.ui.search.SearchBarInterface
import quickbeer.android.ui.searchview.widget.SearchView
import quickbeer.android.util.ktx.viewBinding

class TopBeersFragment : SearchBarFragment(R.layout.beer_list_fragment) {

    private val binding by viewBinding(BeerListFragmentBinding::bind)
    private val beersAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())
    private val viewModel by viewModel<TopBeersViewModel>()

    override val searchHint = R.string.search_hint

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            adapter = beersAdapter
            layoutManager = LinearLayoutManager(context)

            setHasFixedSize(true)
            addItemDecoration(DividerDecoration(context))
        }
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
                    findNavController().navigate(TopBeersFragmentDirections.toSearch(effect.query))
                }
            }
        }
    }

    override fun searchView(): SearchView {
        return binding.searchView
    }

    override fun searchViewModel(): SearchBarInterface {
        return viewModel
    }
}
