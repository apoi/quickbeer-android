/*
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
package quickbeer.android.features.styledetails

import io.reactivex.Observable
import io.reark.reark.data.DataStreamNotification
import quickbeer.android.data.ItemListTimeValidator
import quickbeer.android.data.Reject
import quickbeer.android.data.WithinTime
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.BeerSearchActions
import quickbeer.android.data.actions.StyleActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.providers.ProgressStatusProvider
import quickbeer.android.viewmodels.BeerListViewModel
import quickbeer.android.viewmodels.SearchViewViewModel
import javax.inject.Inject
import javax.inject.Named

class BeersInStyleViewModel @Inject internal constructor(
    @Named("id") private val styleId: Int,
    private val styleActions: StyleActions,
    beerActions: BeerActions,
    beerSearchActions: BeerSearchActions,
    searchViewViewModel: SearchViewViewModel,
    progressStatusProvider: ProgressStatusProvider
) : BeerListViewModel(beerActions, beerSearchActions, searchViewViewModel, progressStatusProvider) {

    override fun dataSource(): Observable<DataStreamNotification<ItemList<String>>> {
        return styleActions.beers(styleId, ItemListTimeValidator(WithinTime.MONTH))
    }

    override fun reloadSource(): Observable<DataStreamNotification<ItemList<String>>> {
        return styleActions.beers(styleId, Reject())
    }
}
