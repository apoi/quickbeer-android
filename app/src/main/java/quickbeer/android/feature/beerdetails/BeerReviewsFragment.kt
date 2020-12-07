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

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.ui.adapter.simple.ListAdapter
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.LoadMoreListener

class BeerReviewsFragment :
    BaseFragment(R.layout.beer_details_reviews_fragment),
    SwipeRefreshLayout.OnRefreshListener {

    private val reviewsAdapter = ListAdapter<ReviewModel>(ReviewTypeFactory())
    private val viewModel by viewModel<BeerDetailsViewModel> {
        parametersOf(requireArguments().getInt(Constants.ID))
    }

    private val loadMoreListener = LoadMoreListener()

    override fun onRefresh() {
        // TODO
    }

    companion object {
        fun create(beerId: Int): Fragment {
            return BeerReviewsFragment().apply {
                arguments = bundleOf(Constants.ID to beerId)
            }
        }
    }
}
