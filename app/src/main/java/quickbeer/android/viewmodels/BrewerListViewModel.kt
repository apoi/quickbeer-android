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
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reark.reark.data.DataStreamNotification
import io.reark.reark.utils.Preconditions.get
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.BrewerActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.providers.ProgressStatusProvider
import timber.log.Timber

abstract class BrewerListViewModel
protected constructor(private val beerActions: BeerActions,
                      private val brewerActions: BrewerActions,
                      private val progressStatusProvider: ProgressStatusProvider)
    : NetworkViewModel<ItemList<String>>() {

    private val brewers = PublishSubject.create<List<BrewerViewModel>>()

    protected abstract fun sourceObservable(): Observable<DataStreamNotification<ItemList<String>>>

    protected open fun reportsProgress(): Boolean {
        return true
    }

    open fun isRefreshable(): Boolean {
        return true
    }

    fun getBrewers(): Observable<List<BrewerViewModel>> {
        return brewers.hide()
    }

    override fun bind(disposable: CompositeDisposable) {
        val sharedObservable = sourceObservable()
                .subscribeOn(Schedulers.computation())
                .publish()

        // Construct progress status. Completed with value means we'll receive onNext.
        disposable.add(sharedObservable
                .filter { !it.isCompletedWithValue }
                .map(toProgressStatus())
                .startWith(NetworkViewModel.ProgressStatus.LOADING)
                .distinctUntilChanged()
                .subscribe { setProgressStatus(it) })

        // Clear list on fetching start
        disposable.add(sharedObservable
                .filter { it.isOngoing }
                .map { emptyList<BrewerViewModel>() }
                .subscribe { brewers.onNext(it) })

        // Actual update
        disposable.add(sharedObservable
                .filter { it.isOnNext }
                .map { get(it.value) }
                .doOnNext { Timber.d("Search finished") }
                .flatMapSingle {
                    Observable.fromIterable(it.items)
                            .map { BrewerViewModel(it, beerActions, brewerActions, progressStatusProvider) }
                            .toList()
                }
                .doOnNext { Timber.d("Publishing ${it.size} brewers") }
                .subscribe { brewers.onNext(it) })

        // Share progress status to progress provider
        if (reportsProgress()) {
            disposable.add(progressStatusProvider
                    .addProgressObservable(sharedObservable.map { it }))
        }

        disposable.add(sharedObservable.connect())
    }
}
