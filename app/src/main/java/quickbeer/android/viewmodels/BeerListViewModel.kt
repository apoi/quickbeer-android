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
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.BeerSearchActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.providers.ProgressStatusProvider
import quickbeer.android.utils.StringUtils
import rx.Observable
import rx.observables.ConnectableObservable
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

abstract class BeerListViewModel
protected constructor(private val beerActions: BeerActions,
                      private val beerSearchActions: BeerSearchActions,
                      private val searchViewViewModel: SearchViewViewModel,
                      private val progressStatusProvider: ProgressStatusProvider)
    : NetworkViewModel<ItemList<String>>() {

    private val source = PublishSubject.create<Observable<DataStreamNotification<ItemList<String>>>>()

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
        return beers.asObservable()
    }

    fun reload() {
        source.onNext(reloadSource())
    }

    override fun bind(subscription: CompositeSubscription) {
        // Switch source according to selector subject
        val sharedObservable: ConnectableObservable<DataStreamNotification<ItemList<String>>> =
                Observable.switchOnNext(source)
                .subscribeOn(Schedulers.computation())
                .publish()

        // Construct progress status. Completed with value means we'll receive onNext.
        subscription.add(sharedObservable
                .filter { !it.isCompletedWithValue }
                .map(toProgressStatus())
                .startWith(NetworkViewModel.ProgressStatus.LOADING)
                .distinctUntilChanged()
                .subscribe { setProgressStatus(it) })

        // Actual update
        subscription.add(sharedObservable
                .filter { it.isOnNext }
                .map { it.value!! }
                .doOnNext { Timber.d("Search finished") }
                .flatMap {
                    Observable.from(it.items)
                        .map { BeerViewModel(it, false, beerActions, progressStatusProvider) }
                        .toList()
                }
                .doOnNext { Timber.d("Publishing %s beers", it.size) }
                .subscribe { beers.onNext(it) })

        // Share progress status to progress provider
        if (reportsProgress()) {
            subscription.add(progressStatusProvider
                    .addProgressObservable(sharedObservable.map { it }))
        }

        subscription.add(sharedObservable.connect())

        // Start with default data source
        source.onNext(dataSource())

        // Switch to search on new search term
        subscription.add(searchViewViewModel
                .getQueryStream()
                .distinctUntilChanged()
                .filter { StringUtils.hasValue(it) }
                .doOnNext { Timber.d("query(%s)", it) }
                .map { beerSearchActions.search(it) }
                .subscribe({ source.onNext(it) }, { Timber.e(it) }))
    }

    override fun hasValue(item: ItemList<String>?): Boolean {
        return item!!.items.isEmpty()
    }
}
