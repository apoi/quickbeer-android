package quickbeer.android.feature.feed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.ListContentBinding
import quickbeer.android.databinding.ListStandaloneFragmentBinding
import quickbeer.android.domain.feed.FeedItem.Type.BEER_ADDED
import quickbeer.android.domain.feed.FeedItem.Type.BEER_RATING
import quickbeer.android.domain.feed.FeedItem.Type.BREWERY_ADDED
import quickbeer.android.navigation.Destination
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.feed.FeedListModel
import quickbeer.android.ui.adapter.feed.FeedTypeFactory
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.setClickListener
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class FeedFragment : BaseFragment(R.layout.list_standalone_fragment) {

    private val binding by viewBinding(
        bind = ListStandaloneFragmentBinding::bind
    )

    private val listBinding by viewBinding(
        bind = ListContentBinding::bind,
        destroyCallback = { it.recyclerView.adapter = null }
    )

    private val args by navArgs<FeedFragmentArgs>()
    private val viewModel by viewModels<FeedViewModel>()
    private val feedAdapter = ListAdapter<FeedListModel>(FeedTypeFactory())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleResId = when (args.mode) {
            MODE_FRIENDS -> R.string.feed_friends
            MODE_LOCAL -> R.string.feed_local
            MODE_GLOBAL -> R.string.feed_global
            else -> error("Invalid mode")
        }

        binding.toolbar.title = getString(titleResId)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        listBinding.recyclerView.apply {
            adapter = feedAdapter
            layoutManager = LinearLayoutManager(context)

            setPadding(0, 0, 0, resources.getDimensionPixelSize(R.dimen.spacing_l))
            setClickListener(::onFeedItemSelected)

            setRecycledViewPool(
                (activity as RecycledPoolHolder)
                    .getPool(PoolType.FEED_LIST, feedAdapter::createPool)
            )
        }
    }

    override fun observeViewState() {
        observe(viewModel.feedListState) { state ->
            when (state) {
                is State.Initial -> Unit
                is State.Loading -> {
                    feedAdapter.setItems(emptyList())
                    listBinding.message.isVisible = false
                    listBinding.progress.show()
                }
                is State.Empty, is State.Error -> {
                    feedAdapter.setItems(emptyList())
                    listBinding.message.text = getString(getErrorText(args.mode))
                    listBinding.message.isVisible = true
                    listBinding.progress.hide()
                }
                is State.Success -> {
                    feedAdapter.setItems(state.value)
                    listBinding.message.isVisible = false
                    listBinding.progress.hide()
                }
            }
        }
    }

    private fun getErrorText(mode: Int): Int {
        return if (mode == MODE_FRIENDS) {
            R.string.feed_friends_error
        } else {
            R.string.message_error
        }
    }

    private fun onFeedItemSelected(model: FeedListModel) {
        when (model.feedItem.type) {
            BEER_ADDED, BEER_RATING -> navigate(Destination.Beer(model.feedItem.beerId()))
            BREWERY_ADDED -> navigate(Destination.Brewer(model.feedItem.brewerId()))
            else -> model.feedItem.link()?.let(::openUri)
        }
    }

    private fun openUri(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        requireContext().startActivity(intent)
    }

    companion object {
        const val MODE_FRIENDS = 0
        const val MODE_GLOBAL = 1
        const val MODE_LOCAL = 2
    }
}
