/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela></antti.poikela>@iki.fi>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.viewmodels

import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.pojos.Beer
import quickbeer.android.providers.ProgressStatusProvider
import rx.Observable
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

class BeerViewModel(val beerId: Int,
                    private val beerActions: BeerActions,
                    private val progressStatusProvider: ProgressStatusProvider)
    : NetworkViewModel<Beer>() {

    private val beer = BehaviorSubject.create<Beer>()

    fun getBeer(): Observable<Beer> {
        return beer.asObservable()
    }

    override fun bind(subscription: CompositeSubscription) {
        val beerSource = beerActions.get(beerId)
                .subscribeOn(Schedulers.computation())
                .publish()

        subscription.add(beerSource
                .map(toProgressStatus())
                .subscribe({ setProgressStatus(it) }, { Timber.e(it) }))

        subscription.add(beerSource
                .filter { it.isOnNext }
                .map { it.value }
                .subscribe({ beer.onNext(it) }, { Timber.e(it) }))

        subscription.add(progressStatusProvider
                .addProgressObservable(beerSource.map { it }))

        subscription.add(beerSource
                .connect())
    }

    override fun hasValue(item: Beer?): Boolean {
        return true
    }
}
