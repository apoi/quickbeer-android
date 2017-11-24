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

import android.os.Bundle
import polanski.option.Option.ofObj
import quickbeer.android.viewmodels.BarcodeSearchViewModel
import quickbeer.android.viewmodels.BeerListViewModel
import timber.log.Timber
import javax.inject.Inject

class BarcodeSearchFragment : BeerListFragment() {

    @Inject
    internal lateinit var barcodeSearchViewModel: BarcodeSearchViewModel

    private var barcode = ""

    private var detailsWasOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ofObj(savedInstanceState ?: arguments)
                .map { state -> state.getString("barcode") }
                .ifSome { value -> barcode = value }
                .ifNone { Timber.w("Expected state for initializing!") }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("barcode", barcode)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        barcodeSearchViewModel.setBarcode(barcode)

    }

    override fun inject() {
        super.inject()
        getComponent().inject(this)
    }

    override fun onResume() {
        super.onResume()

        if (detailsWasOpened) {
            activity.onBackPressed()
        }
    }

    override fun singleResultShouldOpenDetails(): Boolean {
        if (!detailsWasOpened) {
            detailsWasOpened = true
            return true
        }

        return false
    }

    override fun viewModel(): BeerListViewModel {
        return barcodeSearchViewModel
    }

    override fun onQuery(query: String) {
        // No action, new search replaces old results.
    }
}
