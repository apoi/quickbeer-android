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
package quickbeer.android.features.beerdetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.beer_details_fragment_reviews.*
import polanski.option.Option.ofObj
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.core.fragment.BindingBaseFragment
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.injections.IdModule
import quickbeer.android.listeners.LoadMoreListener
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import javax.inject.Inject

class BeerReviewsFragment : BindingBaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    internal lateinit var beerDetailsViewModel: BeerDetailsViewModel

    private val loadMoreListener = LoadMoreListener()

    private var beerId: Int = 0

    companion object {
        fun newInstance(beerId: Int): Fragment {
            return BeerReviewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(Constants.ID_KEY, beerId)
                }
            }
        }
    }

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(subscription: CompositeSubscription) {
            subscription.add(viewModel()
                    .getReviews()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ beer_reviews_view.setReviews(it) }, { Timber.e(it) }))

            subscription.add(loadMoreListener.moreItemsRequestedStream()
                    .subscribe({ viewModel().loadMoreReviews(it) }, { Timber.e(it) }))
        }
    }

    override fun inject() {
        component.plusId(IdModule(beerId))
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ofObj(savedInstanceState ?: arguments)
                .map { state -> state.getInt(Constants.ID_KEY) }
                .ifSome { value -> beerId = value }
                .ifNone { Timber.w("Expected state for initializing!") }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.beer_details_fragment_reviews, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beer_reviews_view.setOnScrollListener(loadMoreListener)
        swipe_refresh_layout.setOnRefreshListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(Constants.ID_KEY, beerId)
        super.onSaveInstanceState(outState)
    }

    override fun viewModel(): BeerDetailsViewModel {
        return beerDetailsViewModel
    }

    override fun dataBinder(): DataBinder {
        return dataBinder
    }

    override fun onRefresh() {
        loadMoreListener.resetState()
        viewModel().reloadReviews()
        swipe_refresh_layout.isRefreshing = false
    }
}
