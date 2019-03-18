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
import io.reactivex.subjects.PublishSubject
import io.reark.reark.utils.RxUtils
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.ReviewActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.pojos.Review
import quickbeer.android.data.stores.ReviewListStore
import quickbeer.android.providers.ProgressStatusProvider
import quickbeer.android.rx.Unit
import timber.log.Timber

class ReviewListViewModel(
    private val beerId: Int,
    private val beerActions: BeerActions,
    private val reviewActions: ReviewActions,
    private val reviewListStore: ReviewListStore,
    private val progressStatusProvider: ProgressStatusProvider
) : NetworkViewModel<ItemList<Review>>() {

    private val loadTrigger = PublishSubject.create<Unit>()

    private val reloadTrigger = PublishSubject.create<Unit>()

    private val reviews = BehaviorSubject.create<List<Review>>()

    fun getReviews(): Observable<List<Review>> {
        return reviews.hide()
    }

    fun fetchReviews(page: Int) {
        Timber.w("fetchReviews(%s)", page)
        beerActions.fetchReviews(beerId, page)
    }

    fun reloadReviews() {
        reloadTrigger.onNext(Unit.DEFAULT)
    }

    private fun getReviewObservable(reviewId: Int): Observable<Review> {
        return reviewActions.get(reviewId)
            .compose { quickbeer.android.rx.RxUtils.pickValue(it) }
            .doOnNext { Timber.v("Received review ${it.id}") }
    }

    override fun bind(disposable: CompositeDisposable) {
        Timber.v("subscribeToDataStoreInternal")

        val reviewSource = loadTrigger.flatMap { beerActions.getReviews(beerId) }
            .publish()

        disposable.add(reviewSource
            .map { toProgressStatus().apply(it) }
            .subscribe { setProgressStatus(it) })

        disposable.add(reviewSource
            .filter { it.isOnNext }
            .map { it.value?.items ?: emptyList() }
            .doOnNext { Timber.d("Review get finished") }
            .flatMap {
                Observable.fromIterable(it)
                    .map { getReviewObservable(it) }
                    .toList()
                    .flatMapObservable { RxUtils.toObservableList(it) }
            }
            .doOnNext { Timber.d("Publishing ${it.size} reviews from the view model") }
            .subscribe { reviews.onNext(it) })

        disposable.add(
            progressStatusProvider
                .addProgressObservable(
                    reviewSource
                        .map({ notification -> notification })))

        disposable.add(
            reviewSource
                .connect())

        disposable.add(reloadTrigger.hide()
            .switchMap { reviewListStore.delete(beerId).toObservable() }
            .doOnEach { reviews.onNext(emptyList<Review>()) }
            .doOnEach { loadTrigger.onNext(Unit.DEFAULT) }
            .subscribe({}, { Timber.e(it) }))

        // Initial trigger at bind time
        loadTrigger.onNext(Unit.DEFAULT)
    }
}
