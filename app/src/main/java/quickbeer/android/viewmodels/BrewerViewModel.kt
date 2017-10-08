/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.viewmodels

import io.reark.reark.data.DataStreamNotification
import io.reark.reark.utils.Preconditions
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.BrewerActions
import quickbeer.android.data.pojos.Brewer
import quickbeer.android.providers.ProgressStatusProvider
import rx.Observable
import rx.Single
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class BrewerViewModel @Inject
internal constructor(@Named("id") val brewerId: Int,
                     @Named("secondaryId") private val beerId: Int,
                     private val beerActions: BeerActions,
                     private val brewerActions: BrewerActions,
                     private val progressStatusProvider: ProgressStatusProvider)
    : NetworkViewModel<Brewer>() {

    constructor(brewerId: Int,
                beerActions: BeerActions,
                brewerActions: BrewerActions,
                progressStatusProvider: ProgressStatusProvider)
            : this(brewerId, -1, beerActions, brewerActions, progressStatusProvider)

    private val brewer = BehaviorSubject.create<Brewer>()

    private val subscription = CompositeSubscription()

    fun getBrewer(): Observable<Brewer> {
        return brewer.asObservable()
    }

    fun reloadBrewerDetails() {
        subscription.add(brewerActions.fetch(brewerId)
                .subscribe({}, { Timber.e(it) }))
    }

    override fun bind(subscription: CompositeSubscription) {
        val brewerSource = brewerSource()
                .subscribeOn(Schedulers.computation())
                .publish()

        subscription.add(brewerSource
                .map(toProgressStatus())
                .subscribe({ this.setProgressStatus(it) }, { Timber.e(it) }))

        subscription.add(brewerSource
                .filter { it.isOnNext }
                .map { it.value }
                .subscribe({ brewer.onNext(it) }, { Timber.e(it) }))

        subscription.add(progressStatusProvider
                .addProgressObservable(brewerSource.map { it }))

        subscription.add(brewerSource
                .connect())
    }

    private fun brewerSource(): Observable<DataStreamNotification<Brewer>> {
        return brewerId()
                .flatMapObservable { brewerActions.get(it) }
    }

    private fun brewerId(): Single<Int> {
        if (brewerId > 0) {
            return Single.just(brewerId)
        } else if (beerId > 0) {
            return beerActions.get(beerId)
                    .filter { it.isOnNext }
                    .map { Preconditions.get(it.value) }
                    .map { Preconditions.get(it.brewerId) }
                    .toSingle()
        } else {
            throw IllegalStateException("No source id!")
        }
    }

    override fun hasValue(item: Brewer?): Boolean {
        return true
    }

    override fun unbind() {
        subscription.clear()
    }
}
