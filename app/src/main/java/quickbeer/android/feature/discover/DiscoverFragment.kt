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
import quickbeer.android.databinding.ListStandaloneFragmentBinding
import quickbeer.android.feature.discover.DiscoverFragmentDirections.Companion.toCountryList
import quickbeer.android.feature.discover.DiscoverFragmentDirections.Companion.toFeed
import quickbeer.android.feature.discover.DiscoverFragmentDirections.Companion.toStyleList
import quickbeer.android.feature.discover.DiscoverFragmentDirections.Companion.toTopBeers
import quickbeer.android.feature.feed.FeedFragment.Companion.MODE_FRIENDS
import quickbeer.android.feature.feed.FeedFragment.Companion.MODE_GLOBAL
import quickbeer.android.feature.feed.FeedFragment.Companion.MODE_LOCAL
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.discover.DiscoverListModel
import quickbeer.android.ui.adapter.discover.DiscoverListModel.Link
import quickbeer.android.ui.adapter.discover.DiscoverTypeFactory
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.setClickListener
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class DiscoverFragment : BaseFragment(R.layout.list_standalone_fragment) {

    private val binding by viewBinding(
        bind = ListStandaloneFragmentBinding::bind
    )

    private val listBinding by viewBinding(
        bind = ListContentBinding::bind,
        destroyCallback = { it.recyclerView.adapter = null }
    )

    private val viewModel by viewModels<DiscoverViewModel>()
    private val discoverAdapter = ListAdapter<DiscoverListModel>(DiscoverTypeFactory())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = getString(R.string.discover_title)

        listBinding.recyclerView.apply {
            adapter = discoverAdapter
            layoutManager = LinearLayoutManager(context)

            setHasFixedSize(true)
            setClickListener(::onItemSelected)

            val spacingL = resources.getDimensionPixelSize(R.dimen.spacing_l)
            setPadding(0, spacingL, 0, spacingL)

            setRecycledViewPool(
                (activity as RecycledPoolHolder)
                    .getPool(PoolType.LINK_LIST, discoverAdapter::createPool)
            )
        }
    }

    override fun observeViewState() {
        observe(viewModel.listState) { state ->
            when (state) {
                is State.Initial -> Unit
                is State.Loading -> {
                    discoverAdapter.setItems(emptyList())
                    listBinding.message.isVisible = false
                    listBinding.progress.show()
                }
                is State.Empty, is State.Error -> {
                    discoverAdapter.setItems(emptyList())
                    listBinding.message.text = getString(R.string.message_error)
                    listBinding.message.isVisible = true
                    listBinding.progress.hide()
                }
                is State.Success -> {
                    discoverAdapter.setItems(state.value)
                    listBinding.message.isVisible = false
                    listBinding.progress.hide()
                }
            }
        }
    }

    private fun onItemSelected(item: DiscoverListModel) {
        when (item.link) {
            Link.TOP_BEERS -> navigate(toTopBeers())
            Link.ALL_COUNTRIES -> navigate(toCountryList())
            Link.ALL_STYLES -> navigate(toStyleList())
            Link.FEED_FRIENDS -> navigate(toFeed(mode = MODE_FRIENDS))
            Link.FEED_LOCAL -> navigate(toFeed(mode = MODE_LOCAL))
            Link.FEED_GLOBAL -> navigate(toFeed(mode = MODE_GLOBAL))
        }
    }
}
