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
import io.reactivex.disposables.CompositeDisposable
import io.reark.reark.data.DataStreamNotification
import quickbeer.android.data.Reject
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.BeerSearchActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.providers.ProgressStatusProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class BeerSearchViewModel @Inject internal constructor(
    @Named("query") private var query: String,
    beerActions: BeerActions,
    private val beerSearchActions: BeerSearchActions,
    private val searchViewViewModel: SearchViewViewModel,
    progressStatusProvider: ProgressStatusProvider
) : BeerListViewModel(beerActions, beerSearchActions, searchViewViewModel, progressStatusProvider) {

    override fun bind(disposable: CompositeDisposable) {
        super.bind(disposable)

        disposable.add(
            searchViewViewModel.getQueryStream()
                .subscribe({ query = it }, { Timber.e(it) }))
    }

    override fun dataSource(): Observable<DataStreamNotification<ItemList<String>>> {
        return beerSearchActions.search(query)
    }

    override fun reloadSource(): Observable<DataStreamNotification<ItemList<String>>> {
        return beerSearchActions.search(query, Reject())
    }
}
