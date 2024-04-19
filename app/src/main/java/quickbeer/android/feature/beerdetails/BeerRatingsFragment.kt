/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.feature.beerdetails

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.ListContentBinding
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.rating.RatingListModel
import quickbeer.android.ui.adapter.rating.RatingTypeFactory
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.LoadMoreListener
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class BeerRatingsFragment : BaseFragment(R.layout.list_fragment) {

    private val binding by viewBinding(
        bind = ListContentBinding::bind,
        destroyCallback = {
            it.recyclerView.adapter = null
            it.recyclerView.clearOnScrollListeners()
        }
    )

    private val viewModel by viewModels<BeerRatingsViewModel>()

    private val ratingsAdapter = ListAdapter<RatingListModel>(RatingTypeFactory())
    private val loadMoreListener = LoadMoreListener()

    private var scrollListener: OnFragmentScrollListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = LinearLayoutManager(context)

        binding.recyclerView.apply {
            adapter = ratingsAdapter
            layoutManager = manager
            addOnScrollListener(loadMoreListener)
        }

        loadMoreListener.moreItemRequestedCallback = viewModel::loadRatingPage
        loadMoreListener.setLayoutManager(manager)

        binding.recyclerView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                scrollListener?.onScrollDown()
            } else if (scrollY < oldScrollY) {
                scrollListener?.onScrollUp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadInitialRatings()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        scrollListener = parentFragment as OnFragmentScrollListener
    }

    override fun onDetach() {
        super.onDetach()
        scrollListener = null
    }

    override fun observeViewState() {
        observe(viewModel.ratingsState) { state ->
            when (state) {
                is State.Initial -> Unit
                is State.Loading -> {
                    binding.message.isVisible = false
                    binding.progress.show()
                }
                is State.Empty -> {
                    ratingsAdapter.setItems(emptyList())
                    binding.message.text = getString(R.string.rating_list_empty)
                    binding.message.isVisible = true
                    binding.progress.hide()
                }
                is State.Success -> {
                    ratingsAdapter.setItems(state.value)
                    binding.message.isVisible = false
                    binding.progress.hide()
                }
                is State.Error -> {
                    ratingsAdapter.setItems(emptyList())
                    binding.message.text = getString(R.string.rating_list_error)
                    binding.message.isVisible = true
                    binding.progress.hide()
                }
            }
        }
    }

    companion object {
        fun create(beerId: Int): Fragment {
            return BeerRatingsFragment().apply {
                arguments = bundleOf(NavParams.ID to beerId)
            }
        }
    }
}
