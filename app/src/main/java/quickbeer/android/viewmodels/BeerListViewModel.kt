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
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reark.reark.data.DataStreamNotification
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.BeerSearchActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.providers.ProgressStatusProvider
import quickbeer.android.utils.kotlin.hasValue
import timber.log.Timber

abstract class BeerListViewModel
protected constructor(
    private val beerActions: BeerActions,
    private val beerSearchActions: BeerSearchActions,
    private val searchViewViewModel: SearchViewViewModel,
    private val progressStatusProvider: ProgressStatusProvider
) : NetworkViewModel<ItemList<String>>() {

    private val source = BehaviorSubject.create<Observable<DataStreamNotification<ItemList<String>>>>()

    private val beers = PublishSubject.create<List<BeerViewModel>>()

    protected abstract fun dataSource(): Observable<DataStreamNotification<ItemList<String>>>

    protected abstract fun reloadSource(): Observable<DataStreamNotification<ItemList<String>>>

    protected open fun reportsProgress(): Boolean {
        return true
    }

    open fun isRefreshable(): Boolean {
        return true
    }

    fun getBeers(): Observable<List<BeerViewModel>> {
        return beers.hide()
    }

    fun reload() {
        Timber.v("reload()")
        source.onNext(reloadSource())
    }

    override fun bind(disposable: CompositeDisposable) {
        Timber.v("bind()")

        // Start with default data source
        source.onNext(dataSource())

        // Switch source according to selector subject
        val sharedObservable =
            Observable.switchOnNext(source)
                .subscribeOn(Schedulers.computation())
                .publish()

        // Construct progress status. Completed with value means we'll receive onNext.
        disposable.add(sharedObservable
            .filter { !it.isCompletedWithValue }
            .map(toProgressStatus())
            .startWith(NetworkViewModel.ProgressStatus.LOADING)
            .distinctUntilChanged()
            .doOnNext { Timber.v("New progress status: $it") }
            .subscribe({ setProgressStatus(it) }, Timber::e))

        // Actual update
        disposable.add(sharedObservable
            .filter { it.isOnNext }
            .map { it.value!! }
            .doOnNext { Timber.d("Search finished") }
            .map { it.items.map { BeerViewModel(it, false, beerActions, progressStatusProvider) } }
            .doOnNext { Timber.d("Publishing %s beers", it.size) }
            .subscribe({ beers.onNext(it) }, Timber::e))

        // Share progress status to progress provider
        if (reportsProgress()) {
            disposable.add(
                progressStatusProvider
                    .addProgressObservable(sharedObservable.map { it }))
        }

        disposable.add(sharedObservable.connect())

        // Switch to search on new search term
        disposable.add(searchViewViewModel
            .getQueryStream()
            .distinctUntilChanged()
            .filter { it.hasValue() }
            .doOnNext {
                Timber.d("query(%s)", it)
                Timber.v("Emptying list for new query")
                beers.onNext(emptyList())
            }
            .map { beerSearchActions.search(it) }
            .subscribe({ source.onNext(it) }, Timber::e))
    }
}
