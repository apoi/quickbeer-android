/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela></antti.poikela>@iki.fi>
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
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.recycler_list.view.*
import quickbeer.android.features.list.BeerListAdapter
import quickbeer.android.utils.StringUtils
import quickbeer.android.utils.kotlin.addOnItemTouchAction
import quickbeer.android.viewmodels.BeerViewModel
import timber.log.Timber

class BeerListView : FrameLayout {

    private val beerListAdapter = BeerListAdapter()
    private val selectedBeerSubject = PublishSubject.create<Int>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onFinishInflate() {
        super.onFinishInflate()

        list_view.apply {
            adapter = beerListAdapter
            layoutManager = LinearLayoutManager(context).apply {
                recycleChildrenOnDetach = true
                addItemDecoration(ListDivider(context, showLast = false))
            }

            addOnItemTouchAction { beerSelected(it) }
        }
    }

    private fun beerSelected(position: Int) {
        val beerId = beerListAdapter.getBeerViewModel(position).beerId

        selectedBeerSubject.onNext(beerId)
    }

    fun selectedBeerStream(): Observable<Int> {
        return selectedBeerSubject.hide()
    }

    fun setBeers(beers: List<BeerViewModel>) {
        Timber.v("Setting ${beers.size} beers to adapter")
        beerListAdapter.set(beers)
    }

    fun setProgressStatus(progressStatus: String) {
        val showProgressText = StringUtils.hasValue(progressStatus) && beerListAdapter.itemCount == 0

        Timber.v("setProgressStatus($progressStatus, $showProgressText)")

        search_status.visibility = if (showProgressText) View.VISIBLE else View.GONE
        search_status.text = progressStatus
    }
}
