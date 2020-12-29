package quickbeer.android.feature.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.ListFragmentBinding
import quickbeer.android.feature.shared.adapter.brewer.BrewerListModel
import quickbeer.android.feature.shared.adapter.brewer.BrewerListTypeFactory
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.simple.ListAdapter
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.setClickListener
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

class SearchBrewersFragment : BaseFragment(R.layout.list_fragment) {

    private val binding by viewBinding(ListFragmentBinding::bind)
    private val viewModel by sharedViewModel<SearchViewModel>()
    private val brewersAdapter = ListAdapter<BrewerListModel>(BrewerListTypeFactory())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            adapter = brewersAdapter
            layoutManager = LinearLayoutManager(context).apply {
                recycleChildrenOnDetach = true
            }

            setHasFixedSize(true)
            addItemDecoration(DividerDecoration(context))
            setClickListener(::onBrewerSelected)

            setRecycledViewPool(
                (activity as RecycledPoolHolder)
                    .getPool(PoolType.BREWER_LIST, brewersAdapter::createPool)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
    }

    override fun observeViewState() {
        observe(viewModel.brewerResults) { state ->
            when (state) {
                State.Loading -> {
                    brewersAdapter.setItems(emptyList())
                    binding.recyclerView.scrollToPosition(0)
                    binding.message.isVisible = false
                    binding.progress.show()
                }
                State.Empty -> {
                    brewersAdapter.setItems(emptyList())
                    binding.message.text = getString(R.string.message_empty)
                    binding.message.isVisible = true
                    binding.progress.hide()
                }
                is State.Success -> {
                    brewersAdapter.setItems(state.value)
                    binding.message.isVisible = false
                    binding.progress.hide()
                }
                is State.Error -> {
                    brewersAdapter.setItems(emptyList())
                    binding.message.text = getString(R.string.message_error)
                    binding.message.isVisible = true
                    binding.progress.hide()
                }
            }
        }
    }

    private fun onBrewerSelected(brewer: BrewerListModel) {
        navigate(SearchFragmentDirections.toBeer(brewer.id))
    }
}
