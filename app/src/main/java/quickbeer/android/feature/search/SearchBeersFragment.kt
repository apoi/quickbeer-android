package quickbeer.android.feature.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.ListFragmentBinding
import quickbeer.android.navigation.Destination
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListTypeFactory
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.setClickListener
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType
import quickbeer.android.util.exception.AppException
import quickbeer.android.util.ktx.getMessage
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

class SearchBeersFragment : BaseFragment(R.layout.list_fragment) {

    private val binding by viewBinding(ListFragmentBinding::bind)
    private val viewModel by sharedViewModel<SearchViewModel>()
    private val beersAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())

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
        super.onDestroyView()
        binding.recyclerView.adapter = null
    }

    override fun observeViewState() {
        observe(viewModel.beerResults) { state ->
            when (state) {
                is State.Loading -> {
                    beersAdapter.setItems(state.value ?: emptyList())
                    binding.recyclerView.scrollToPosition(0)
                    binding.message.text = getString(R.string.search_progress)
                    binding.message.isVisible = state.value?.isNotEmpty() != true
                }
                State.Empty -> {
                    beersAdapter.setItems(emptyList())
                    binding.message.text = getString(R.string.message_empty)
                    binding.message.isVisible = true
                }
                is State.Success -> {
                    val scrollY = binding.recyclerView.computeVerticalScrollOffset()
                    beersAdapter.setItems(state.value)
                    // Only reset scrolling if already at the top
                    if (scrollY == 0) binding.recyclerView.scrollToPosition(0)
                    binding.message.isVisible = false
                }
                is State.Error -> {
                    beersAdapter.setItems(emptyList())
                    binding.message.text = getError(state.cause)
                    binding.message.isVisible = true
                }
            }
        }
    }

    private fun getError(error: Throwable): String {
        return when (error) {
            AppException.RepositoryKeyEmpty -> ""
            AppException.RepositoryKeyInvalid -> "Query needs to be at least four characters."
            else -> error.getMessage(::getString)
        }
    }

    private fun onBeerSelected(beer: BeerListModel) {
        navigate(Destination.Beer(beer.id))
    }
}
