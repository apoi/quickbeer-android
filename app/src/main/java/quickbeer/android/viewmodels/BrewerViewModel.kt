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
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reark.reark.data.DataStreamNotification
import quickbeer.android.data.HasDetailsData
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.BrewerActions
import quickbeer.android.data.pojos.Brewer
import quickbeer.android.providers.ProgressStatusProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class BrewerViewModel @Inject internal constructor(
    @Named("id") val brewerId: Int,
    @Named("secondaryId") private val beerId: Int,
    private val beerActions: BeerActions,
    private val brewerActions: BrewerActions,
    private val progressStatusProvider: ProgressStatusProvider
) : NetworkViewModel<Brewer>() {

    constructor(
        brewerId: Int,
        beerActions: BeerActions,
        brewerActions: BrewerActions,
        progressStatusProvider: ProgressStatusProvider
    ) : this(brewerId, -1, beerActions, brewerActions, progressStatusProvider)

    private val brewer = BehaviorSubject.create<Brewer>()

    private val disposable = CompositeDisposable()

    fun getBrewer(): Observable<Brewer> {
        return brewer.hide()
    }

    fun reloadBrewerDetails() {
        disposable.add(getBrewerId()
            .flatMap { brewerActions.fetch(it) }
            .subscribe({}, Timber::e))
    }

    override fun bind(disposable: CompositeDisposable) {
        val brewerSource = brewerSource()
            .subscribeOn(Schedulers.computation())
            .publish()

        disposable.add(
            brewerSource
                .map(toProgressStatus())
                .subscribe({ setProgressStatus(it) }, Timber::e))

        disposable.add(brewerSource
            .filter { it.isOnNext }
            .map { it.value }
            .subscribe({ brewer.onNext(it!!) }, Timber::e))

        disposable.add(
            progressStatusProvider
                .addProgressObservable(brewerSource.map { it }))

        disposable.add(
            brewerSource
                .connect())
    }

    private fun brewerSource(): Observable<DataStreamNotification<Brewer>> {
        return getBrewerId()
            .flatMapObservable { brewerActions.get(it) }
    }

    private fun getBrewerId(): Single<Int> {
        if (brewerId > 0) {
            return Single.just(brewerId)
        } else if (beerId > 0) {
            return beerActions.get(beerId, HasDetailsData())
                .filter { it.isOnNext }
                .map { it.value!! }
                .map { it.brewerId!! }
                .firstOrError()
        } else {
            throw IllegalStateException("No source id!")
        }
    }

    override fun unbind() {
        disposable.clear()
    }
}
