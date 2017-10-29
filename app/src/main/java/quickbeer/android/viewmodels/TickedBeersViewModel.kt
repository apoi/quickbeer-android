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
import polanski.option.Option
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.BeerSearchActions
import quickbeer.android.data.actions.ReviewActions
import quickbeer.android.data.actions.UserActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.pojos.User
import quickbeer.android.providers.ProgressStatusProvider
import rx.Observable
import rx.Single
import javax.inject.Inject

class TickedBeersViewModel @Inject
internal constructor(private val userActions: UserActions,
                     private val reviewActions: ReviewActions,
                     beerActions: BeerActions,
                     beerSearchActions: BeerSearchActions,
                     searchViewViewModel: SearchViewViewModel,
                     progressStatusProvider: ProgressStatusProvider)
    : BeerListViewModel(beerActions, beerSearchActions, searchViewViewModel, progressStatusProvider) {

    fun getUser(): Observable<Option<User>> {
        return userActions.getUser()
    }

    fun refreshTicks(user: User) {
        reviewActions.fetchTicks(user.id.toString())
    }

    override fun dataSource(): Observable<DataStreamNotification<ItemList<String>>> {
        return userActions.getUser()
                .flatMap { userOption -> userOption.match(
                        { reviewActions.getTicks(it.id.toString()) },
                        { Observable.just(DataStreamNotification.completedWithoutValue<ItemList<String>>()) })
                }
    }

    override fun reloadSource(): Observable<DataStreamNotification<ItemList<String>>> {
        return userActions.getUser()
                .flatMapSingle { userOption -> userOption.match(
                        { reviewActions.fetchTicks(it.id.toString()) },
                        { Single.just(false) })
                }
                .flatMap { dataSource() }
    }
}
