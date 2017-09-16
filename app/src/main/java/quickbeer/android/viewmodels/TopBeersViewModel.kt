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

import io.reark.reark.data.DataStreamNotification
import quickbeer.android.data.DataLayer
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.providers.ProgressStatusProvider
import rx.Observable
import javax.inject.Inject

class TopBeersViewModel @Inject
internal constructor(getBeer: DataLayer.GetBeer,
                     getBeerSearch: DataLayer.GetBeerSearch,
                     val getTopBeers: DataLayer.GetTopBeers,
                     searchViewViewModel: SearchViewViewModel,
                     progressStatusProvider: ProgressStatusProvider)
    : BeerListViewModel(getBeer, getBeerSearch, searchViewViewModel, progressStatusProvider) {

    override fun dataSource(): Observable<DataStreamNotification<ItemList<String>>> {
        return getTopBeers.call()
    }

    override fun reloadSource(): Observable<DataStreamNotification<ItemList<String>>> {
        return Observable.empty()
    }
}
