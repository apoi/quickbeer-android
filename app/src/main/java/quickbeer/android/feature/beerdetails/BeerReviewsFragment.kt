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

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.BeerDetailsReviewsFragmentBinding
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.review.ReviewListModel
import quickbeer.android.ui.adapter.review.ReviewTypeFactory
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.LoadMoreListener
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

class BeerReviewsFragment :
    BaseFragment(R.layout.beer_details_reviews_fragment) {

    private val binding by viewBinding(BeerDetailsReviewsFragmentBinding::bind)
    private val reviewsAdapter = ListAdapter<ReviewListModel>(ReviewTypeFactory())
    private val viewModel by viewModel<BeerReviewsViewModel> {
        parametersOf(requireArguments().getInt(NavParams.ID))
    }

    private val loadMoreListener = LoadMoreListener()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = LinearLayoutManager(context)

        binding.list.apply {
            adapter = reviewsAdapter
            layoutManager = manager
            addOnScrollListener(loadMoreListener)
        }

        loadMoreListener.moreItemRequestedCallback = viewModel::loadReviewPage
        loadMoreListener.setLayoutManager(manager)
    }

    override fun onDestroyView() {
        binding.list.adapter = null
        binding.list.clearOnScrollListeners()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadInitialReviews()
    }

    override fun observeViewState() {
        observe(viewModel.reviewsState) { state ->
            when (state) {
                is State.Loading -> {
                    binding.message.isVisible = false
                    binding.progress.show()
                }
                is State.Empty -> {
                    reviewsAdapter.setItems(emptyList())
                    binding.message.text = getString(R.string.review_list_empty)
                    binding.message.isVisible = true
                    binding.progress.hide()
                }
                is State.Success -> {
                    reviewsAdapter.setItems(state.value)
                    binding.message.isVisible = false
                    binding.progress.hide()
                }
                is State.Error -> {
                    reviewsAdapter.setItems(emptyList())
                    binding.message.text = getString(R.string.review_list_error)
                    binding.message.isVisible = true
                    binding.progress.hide()
                }
            }
        }
    }

    companion object {
        fun create(beerId: Int): Fragment {
            return BeerReviewsFragment().apply {
                arguments = bundleOf(NavParams.ID to beerId)
            }
        }
    }
}
