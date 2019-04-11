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
import io.reactivex.subjects.BehaviorSubject
import io.reark.reark.data.DataStreamNotification
import quickbeer.android.data.HasBeerBasicData
import quickbeer.android.data.HasBeerDetailsData
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.pojos.Beer
import quickbeer.android.providers.ProgressStatusProvider
import timber.log.Timber

class BeerViewModel(
    val beerId: Int,
    private val detailed: Boolean,
    private val beerActions: BeerActions,
    private val progressStatusProvider: ProgressStatusProvider
) : NetworkViewModel<Beer>() {

    private val beer = BehaviorSubject.create<Beer>()

    fun getBeer(): Observable<Beer> {
        return beer.hide()
    }

    override fun bind(disposable: CompositeDisposable) {
        val beerSource = getBeer(beerId)
            .publish()

        disposable.add(beerSource
            .map(toProgressStatus())
            .subscribe({ setProgressStatus(it) }, Timber::e))

        disposable.add(beerSource
            .filter { it.isOnNext }
            .map { it.value }
            .subscribe({ beer.onNext(it!!) }, Timber::e))

        disposable.add(
            progressStatusProvider
                .addProgressObservable(beerSource.map { it }))

        disposable.add(beerSource.connect())
    }

    private fun getBeer(beerId: Int): Observable<DataStreamNotification<Beer>> {
        val dataRequirement = if (detailed) HasBeerDetailsData() else HasBeerBasicData()
        return beerActions.get(beerId, dataRequirement)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as BeerViewModel

        if (beerId != other.beerId) return false

        return true
    }

    override fun hashCode(): Int {
        return beerId
    }
}
