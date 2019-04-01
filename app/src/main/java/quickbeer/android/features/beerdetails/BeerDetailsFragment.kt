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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.beer_details_fragment_details.*
import polanski.option.Option.ofObj
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.analytics.Events.Action
import quickbeer.android.core.fragment.BindingBaseFragment
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.injections.IdModule
import timber.log.Timber
import javax.inject.Inject

class BeerDetailsFragment : BindingBaseFragment(), RatingBar.OnRatingBarChangeListener,
    SwipeRefreshLayout.OnRefreshListener {

    @Inject
    internal lateinit var beerDetailsViewModel: BeerDetailsViewModel

    @Inject
    internal lateinit var analytics: Analytics

    private var beerId: Int = 0

    companion object {
        fun newInstance(beerId: Int): Fragment {
            return BeerDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(Constants.ID_KEY, beerId)
                }
            }
        }
    }

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(disposable: CompositeDisposable) {
            disposable.add(
                viewModel()
                    .getBeer()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ beer_details_view.setBeer(it) }, { Timber.e(it) }))

            disposable.add(
                viewModel()
                    .getBrewer()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ beer_details_view.setBrewer(it) }, { Timber.e(it) }))

            disposable.add(
                viewModel()
                    .getUser()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ beer_details_view.setUser(it) }, { Timber.e(it) }))

            // Re-emit beer on tick failure to reset rating bar
            disposable.add(viewModel()
                .tickSuccessStatus()
                .filter { success -> !success }
                .flatMap { viewModel().getBeer() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ beer_details_view.setBeer(it) }, { Timber.e(it) }))
        }
    }

    override fun inject() {
        getComponent().plusId(IdModule(beerId))
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
        return inflater.inflate(R.layout.beer_details_fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beer_details_view.setRatingBarChangeListener(this)
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

    override fun onRatingChanged(ratingBar: RatingBar, rating: Float, fromUser: Boolean) {
        if (fromUser) {
            viewModel().tickBeer(rating.toInt())
            analytics.createEvent(if (rating > 0) Action.TICK_ADD else Action.TICK_REMOVE)
        }
    }

    override fun onRefresh() {
        viewModel().reloadBeerDetails()
        swipe_refresh_layout.isRefreshing = false
    }
}
