package quickbeer.android.feature.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.Flow
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.ListContentBinding
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.setSingleTypeClickListener
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType
import quickbeer.android.util.exception.AppException
import quickbeer.android.util.ktx.getMessage
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

abstract class SearchTabFragment<T : ListItem> : BaseFragment(R.layout.list_fragment) {

    private val binding by viewBinding(
        bind = ListContentBinding::bind,
        destroyCallback = { it.recyclerView.adapter = null }
    )

    protected val viewModel by activityViewModels<SearchViewModel>()

    protected abstract val resultAdapter: ListAdapter<T>
    protected abstract val resultPoolType: PoolType
    protected abstract fun resultFlow(): Flow<State<List<T>>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            adapter = resultAdapter
            layoutManager = LinearLayoutManager(context)

            setHasFixedSize(true)
            addItemDecoration(DividerDecoration(context))
            setSingleTypeClickListener(::onItemSelected)

            setRecycledViewPool(
                (activity as RecycledPoolHolder)
                    .getPool(resultPoolType, resultAdapter::createPool)
            )
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun observeViewState() {
        observe(resultFlow()) { state ->
            when (state) {
                is State.Initial -> {
                    binding.message.text = getString(R.string.search_initial)
                    binding.message.isVisible = true
                }
                is State.Loading -> {
                    binding.recyclerView.scrollToPosition(0)
                    binding.message.text = getString(R.string.search_progress)
                    binding.message.isVisible = state.value?.isNotEmpty() != true
                    resultAdapter.setItems(state.value ?: emptyList())
                }
                is State.Empty -> {
                    binding.message.text = getString(R.string.message_empty)
                    binding.message.isVisible = true
                    resultAdapter.setItems(emptyList())
                }
                is State.Success -> {
                    val scrollY = binding.recyclerView.computeVerticalScrollOffset()
                    // Only reset scrolling if already at the top and this isn't restore
                    if (scrollY == 0 && lifecycle.currentState == Lifecycle.State.RESUMED) {
                        binding.recyclerView.scrollToPosition(0)
                    }
                    binding.message.isVisible = false
                    resultAdapter.setItems(state.value)
                }
                is State.Error -> {
                    binding.message.text = getError(state.cause)
                    binding.message.isVisible = true
                    resultAdapter.setItems(emptyList())
                }
            }
        }
    }

    private fun getError(error: Throwable): String {
        return when (error) {
            AppException.RepositoryKeyEmpty -> getString(R.string.search_initial)
            AppException.RepositoryKeyInvalid -> getString(R.string.search_min_length)
            else -> error.getMessage(::getString)
        }
    }

    abstract fun onItemSelected(item: T)
}
