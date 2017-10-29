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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.brewer_tab_fragment.*
import kotlinx.android.synthetic.main.recycler_list.*
import quickbeer.android.R
import quickbeer.android.core.fragment.BindingBaseFragment
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.features.brewerdetails.BrewerDetailsActivity
import quickbeer.android.providers.ResourceProvider
import quickbeer.android.viewmodels.BrewerListViewModel
import quickbeer.android.viewmodels.BrewerViewModel
import quickbeer.android.viewmodels.NetworkViewModel.ProgressStatus
import quickbeer.android.viewmodels.SearchViewViewModel
import quickbeer.android.viewmodels.SearchViewViewModel.Mode
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import javax.inject.Inject

abstract class BrewerListFragment : BindingBaseFragment() {

    @Inject
    internal lateinit var resourceProvider: ResourceProvider

    @Inject
    internal lateinit var searchViewViewModel: SearchViewViewModel

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(subscription: CompositeSubscription) {
            subscription.add(list_layout.selectedBrewerStream()
                    .doOnNext { Timber.d("Selected brewer %s", it) }
                    .subscribe({ openBrewerDetails(it) }, { Timber.e(it) }))

            subscription.add(viewModel()
                    .getBrewers()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ handleResultList(it) }, { Timber.e(it) }))

            subscription.add(viewModel().getProgressStatus()
                    .map({ toStatusValue(it) })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ list_layout.setProgressStatus(it) }, { Timber.e(it) }))

            subscription.add(searchViewViewModel.getQueryStream()
                    .subscribe({ onQuery(it) }, { Timber.e(it) }))
        }
    }

    private fun handleResultList(brewerList: List<BrewerViewModel>) {
        if (brewerList.size == 1 && singleResultShouldOpenDetails()) {
            openBrewerDetails(brewerList[0].brewerId)
        } else {
            list_layout.setBrewers(brewerList)
        }
    }

    protected fun singleResultShouldOpenDetails(): Boolean {
        return false
    }

    protected fun openBrewerDetails(brewerId: Int) {
        val intent = Intent(activity, BrewerDetailsActivity::class.java)
        intent.putExtra("brewerId", brewerId)
        startActivity(intent)
    }

    override fun inject() {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayout(), container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        swipe_refresh_layout.isEnabled = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        searchViewViewModel.setMode(Mode.SEARCH, resourceProvider.getString(R.string.search_box_hint_search_beers))
    }

    open protected fun getLayout(): Int {
        return R.layout.brewer_list_fragment
    }

    protected open fun toStatusValue(progressStatus: ProgressStatus): String {
        return when (progressStatus) {
            ProgressStatus.LOADING -> getString(R.string.search_status_loading)
            ProgressStatus.ERROR -> getString(R.string.search_status_error)
            ProgressStatus.EMPTY -> getString(R.string.search_no_results)
            else -> ""
        }
    }

    abstract override fun viewModel(): BrewerListViewModel

    protected abstract fun onQuery(query: String)

    override fun dataBinder(): DataBinder {
        return dataBinder
    }
}
