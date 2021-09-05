package quickbeer.android.feature.discover

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.ListContentBinding
import quickbeer.android.navigation.Destination
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.brewer.BrewerListModel
import quickbeer.android.ui.adapter.brewer.BrewerListTypeFactory
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.setClickListener
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class RecentBrewersFragment : BaseFragment(R.layout.list_fragment) {

    private val binding by viewBinding(ListContentBinding::bind)
    private val viewModel by viewModels<DiscoverViewModel>()
    private val brewersAdapter = ListAdapter<BrewerListModel>(BrewerListTypeFactory())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.message.text = getString(R.string.message_start)

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
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    override fun observeViewState() {
        observe(viewModel.recentBrewersState) { state ->
            when (state) {
                is State.Loading -> {
                    brewersAdapter.setItems(emptyList())
                    binding.message.isVisible = false
                    binding.progress.show()
                }
                is State.Empty -> {
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
        navigate(Destination.Brewer(brewer.brewerId))
    }
}
