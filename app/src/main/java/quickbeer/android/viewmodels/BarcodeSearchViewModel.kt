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
package quickbeer.android.viewmodels

import io.reactivex.Observable
import io.reark.reark.data.DataStreamNotification
import quickbeer.android.data.actions.BarcodeActions
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.BeerSearchActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.providers.ProgressStatusProvider
import javax.inject.Inject

class BarcodeSearchViewModel @Inject internal constructor(
    beerActions: BeerActions,
    beerSearchActions: BeerSearchActions,
    private val barcodeActions: BarcodeActions,
    searchViewViewModel: SearchViewViewModel,
    progressStatusProvider: ProgressStatusProvider
) : BeerListViewModel(beerActions, beerSearchActions, searchViewViewModel, progressStatusProvider) {

    private var barcode = ""

    fun setBarcode(barcode: String) {
        this.barcode = barcode
    }

    override fun dataSource(): Observable<DataStreamNotification<ItemList<String>>> {
        return barcodeActions.get(barcode)
    }

    override fun reloadSource(): Observable<DataStreamNotification<ItemList<String>>> {
        return dataSource()
            .mergeWith(barcodeActions.fetch(barcode))
    }
}
