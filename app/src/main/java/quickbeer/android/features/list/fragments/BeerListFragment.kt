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
package quickbeer.android.features.list.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reark.reark.utils.Preconditions.get
import kotlinx.android.synthetic.main.beer_list_fragment_standalone.*
import kotlinx.android.synthetic.main.recycler_list.*
import quickbeer.android.R
import quickbeer.android.core.fragment.BindingBaseFragment
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.features.beerdetails.BeerDetailsActivity
import quickbeer.android.providers.ResourceProvider
import quickbeer.android.viewmodels.BeerListViewModel
import quickbeer.android.viewmodels.BeerViewModel
import quickbeer.android.viewmodels.NetworkViewModel.ProgressStatus
import quickbeer.android.viewmodels.SearchViewViewModel
import quickbeer.android.viewmodels.SearchViewViewModel.Mode
import timber.log.Timber
import javax.inject.Inject

abstract class BeerListFragment : BindingBaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    internal lateinit var resourceProvider: ResourceProvider

    @Inject
    internal lateinit var searchViewViewModel: SearchViewViewModel

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(disposable: CompositeDisposable) {
            disposable.add(list_layout.selectedBeerStream()
                .doOnNext { Timber.d("Selected beer %s", it) }
                .subscribe({ openBeerDetails(it) }, { Timber.e(it) }))

            disposable.add(
                viewModel().getBeers()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ handleResultList(it) }, { Timber.e(it) }))

            disposable.add(viewModel().getProgressStatus()
                .map { toStatusValue(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list_layout.setProgressStatus(it) }, { Timber.e(it) }))

            disposable.add(
                get(searchViewViewModel)
                    .getQueryStream()
                    .subscribe({ onQuery(it) }, { Timber.e(it) }))
        }
    }

    protected fun handleResultList(beerList: List<BeerViewModel>) {
        if (beerList.size == 1 && singleResultShouldOpenDetails()) {
            openBeerDetails(beerList[0].beerId)
        } else {
            list_layout.setBeers(beerList)
        }
    }

    protected open fun singleResultShouldOpenDetails(): Boolean {
        return false
    }

    protected fun openBeerDetails(beerId: Int) {
        val intent = Intent(activity, BeerDetailsActivity::class.java)
        intent.putExtra("beerId", beerId)
        startActivity(intent)
    }

    override fun inject() {
        getComponent().inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swipe_refresh_layout.isEnabled = viewModel().isRefreshable()
        swipe_refresh_layout.setOnRefreshListener(this)

        searchViewViewModel.setMode(Mode.SEARCH, get(resourceProvider).getString(R.string.search_box_hint_search_beers))
    }

    protected open fun getLayout(): Int {
        return R.layout.beer_list_fragment_standalone
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayout(), container, false)
    }

    protected open fun toStatusValue(progressStatus: ProgressStatus): String {
        return when (progressStatus) {
            ProgressStatus.LOADING -> getString(R.string.search_status_loading)
            ProgressStatus.ERROR -> getString(R.string.search_status_error)
            ProgressStatus.EMPTY -> getString(R.string.search_no_results)
            else -> ""
        }
    }

    abstract override fun viewModel(): BeerListViewModel

    protected abstract fun onQuery(query: String)

    override fun dataBinder(): DataBinder {
        return dataBinder
    }

    override fun onRefresh() {
        viewModel().reload()
        swipe_refresh_layout.isRefreshing = false
    }
}
