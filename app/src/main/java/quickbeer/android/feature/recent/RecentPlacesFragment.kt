package quickbeer.android.feature.recent

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
import quickbeer.android.ui.adapter.place.PlaceListModel
import quickbeer.android.ui.adapter.place.PlaceListTypeFactory
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.setClickListener
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class RecentPlacesFragment : BaseFragment(R.layout.list_fragment) {

    private val binding by viewBinding(
        bind = ListContentBinding::bind,
        destroyCallback = { it.recyclerView.adapter = null }
    )

    private val viewModel by viewModels<RecentPlacesViewModel>()
    private val placesAdapter = ListAdapter<PlaceListModel>(PlaceListTypeFactory())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.message.text = getString(R.string.message_start)

        binding.recyclerView.apply {
            adapter = placesAdapter
            layoutManager = LinearLayoutManager(context)

            setHasFixedSize(true)
            addItemDecoration(DividerDecoration(context))
            setClickListener(::onPlaceSelected)

            setRecycledViewPool(
                (activity as RecycledPoolHolder)
                    .getPool(PoolType.PLACE_LIST, placesAdapter::createPool)
            )
        }
    }

    override fun observeViewState() {
        observe(viewModel.recentPlacesState) { state ->
            when (state) {
                is State.Initial -> Unit
                is State.Loading -> {
                    placesAdapter.setItems(emptyList())
                    binding.message.isVisible = false
                    binding.progress.show()
                }
                is State.Empty -> {
                    placesAdapter.setItems(emptyList())
                    binding.message.text = getString(R.string.message_start)
                    binding.message.isVisible = true
                    binding.progress.hide()
                }
                is State.Success -> {
                    placesAdapter.setItems(state.value)
                    binding.message.isVisible = false
                    binding.progress.hide()
                }
                is State.Error -> {
                    placesAdapter.setItems(emptyList())
                    binding.message.text = getString(R.string.message_error)
                    binding.message.isVisible = true
                    binding.progress.hide()
                }
            }
        }
    }

    private fun onPlaceSelected(place: PlaceListModel) {
        navigate(Destination.Place(place.placeId))
    }
}
