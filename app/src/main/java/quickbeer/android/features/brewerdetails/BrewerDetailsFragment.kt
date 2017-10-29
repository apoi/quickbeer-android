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
package quickbeer.android.features.brewerdetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.brewer_details_fragment_details.*
import polanski.option.Option.ofObj
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.core.fragment.BindingBaseFragment
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.injections.IdModule
import quickbeer.android.viewmodels.BrewerViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import javax.inject.Inject

class BrewerDetailsFragment : BindingBaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    internal lateinit var brewerViewModel: BrewerViewModel

    private var brewerId: Int = 0

    companion object {
        fun newInstance(brewerId: Int): Fragment {
            val fragment = BrewerDetailsFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.ID_KEY, brewerId)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(subscription: CompositeSubscription) {
            subscription.add(viewModel()
                    .getBrewer()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ brewer_details_view.setBrewer(it) }, { Timber.e(it) }))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ofObj(savedInstanceState ?: arguments)
                .map { it.getInt(Constants.ID_KEY) }
                .ifSome { brewerId = it }
                .ifNone { Timber.w("Expected state for initializing!") }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.brewer_details_fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_refresh_layout.setOnRefreshListener(this)
    }

    override fun inject() {
        component.plusId(IdModule(brewerId))
                .inject(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(Constants.ID_KEY, brewerId)
        super.onSaveInstanceState(outState)
    }

    override fun viewModel(): BrewerViewModel {
        return brewerViewModel
    }

    override fun dataBinder(): DataBinder {
        return dataBinder
    }

    override fun onRefresh() {
        viewModel().reloadBrewerDetails()
        swipe_refresh_layout.isRefreshing = false
    }
}
