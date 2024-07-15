package quickbeer.android.feature.topbeers

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.ListContentBinding
import quickbeer.android.databinding.ListStandaloneFragmentBinding
import quickbeer.android.navigation.Destination
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListTypeFactory
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.setClickListener
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class TopBeersFragment : BaseFragment(R.layout.list_standalone_fragment) {

    private val binding by viewBinding(
        bind = ListStandaloneFragmentBinding::bind
    )

    private val listBinding by viewBinding(
        bind = ListContentBinding::bind,
        destroyCallback = { it.recyclerView.adapter = null }
    )

    private val viewModel by viewModels<TopBeersViewModel>()
    private val beersAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = getString(R.string.discover_top_beers)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        listBinding.recyclerView.apply {
            adapter = beersAdapter
            layoutManager = LinearLayoutManager(context)

            setHasFixedSize(true)
            addItemDecoration(DividerDecoration(context))
            setClickListener(::onBeerSelected)

            setRecycledViewPool(
                (activity as RecycledPoolHolder)
                    .getPool(PoolType.BEER_LIST, beersAdapter::createPool)
            )
        }
    }

    override fun observeViewState() {
        observe(viewModel.beerListState) { state ->
            when (state) {
                is State.Initial -> Unit
                is State.Loading -> {
                    beersAdapter.setItems(emptyList())
                    listBinding.message.isVisible = false
                    listBinding.progress.show()
                }
                is State.Empty, is State.Error -> {
                    beersAdapter.setItems(emptyList())
                    listBinding.message.text = getString(R.string.message_error)
                    listBinding.message.isVisible = true
                    listBinding.progress.hide()
                }
                is State.Success -> {
                    beersAdapter.setItems(state.value)
                    listBinding.message.isVisible = false
                    listBinding.progress.hide()
                }
            }
        }
    }

    private fun onBeerSelected(beer: BeerListModel) {
        navigate(Destination.Beer(beer.beerId))
    }
}
