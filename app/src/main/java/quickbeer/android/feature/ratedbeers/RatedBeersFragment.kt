package quickbeer.android.feature.ratedbeers

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.RatedBeersFragmentBinding
import quickbeer.android.databinding.TickedBeersFragmentBinding
import quickbeer.android.navigation.Destination
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListTypeFactory
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.setClickListener
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class RatedBeersFragment : BaseFragment(R.layout.rated_beers_fragment) {

    private val binding by viewBinding(
        bind = RatedBeersFragmentBinding::bind,
        destroyCallback = { it.recyclerView.adapter = null }
    )

    private val viewModel by viewModels<RatedBeersViewModel>()
    private val beersAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

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
                    .getPool(RecycledPoolHolder.PoolType.BEER_LIST, beersAdapter::createPool)
            )
        }
    }

    override fun observeViewState() {
        observe(viewModel.viewState) { state ->
            when (state) {
                is State.Initial -> Unit
                is State.Loading -> {
                    beersAdapter.setItems(emptyList())
                    binding.message.isVisible = false
                    binding.progress.show()
                }
                is State.Empty -> {
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

    private fun onBeerSelected(beer: BeerListModel) {
        navigate(Destination.Beer(beer.id))
    }
}
