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
import io.reark.reark.utils.RxUtils
import quickbeer.android.data.ItemListTimeValidator
import quickbeer.android.data.Reject
import quickbeer.android.data.WithinTime
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.ReviewActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.pojos.Review
import quickbeer.android.data.stores.ReviewListStore
import quickbeer.android.providers.ProgressStatusProvider
import timber.log.Timber

class ReviewListViewModel(
    private val beerId: Int,
    private val beerActions: BeerActions,
    private val reviewActions: ReviewActions,
    private val reviewListStore: ReviewListStore,
    private val progressStatusProvider: ProgressStatusProvider
) : NetworkViewModel<ItemList<Review>>() {

    private val reviews = BehaviorSubject.create<List<Review>>()

    fun getReviews(): Observable<List<Review>> {
        return reviews.hide()
    }

    fun fetchReviews(page: Int) {
        Timber.w("fetchReviews($page)")

        beerActions.getReviews(beerId, page)
    }

    fun reloadReviews() {
        Timber.w("reloadReviews()")

        disposable.add(beerActions
            .getReviews(beerId, validator = Reject())
            .doOnSubscribe {
                // Clear to show we're reloading
                reviews.onNext(emptyList())
            }
            .subscribe({}, Timber::e))
    }

    private fun getReviewObservable(reviewId: Int): Observable<Review> {
        return reviewActions.get(reviewId)
            .compose { quickbeer.android.rx.RxUtils.pickValue(it) }
            .doOnNext { Timber.v("Received review ${it.id}") }
    }

    override fun bind(disposable: CompositeDisposable) {
        Timber.v("bind")

        val reviewSource = beerActions
            .getReviews(beerId, validator = ItemListTimeValidator(WithinTime.WEEK))
            .publish()

        disposable.add(reviewSource
            .map { toProgressStatus().apply(it) }
            .subscribe({ setProgressStatus(it) }, { Timber.e(it) }))

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
            .subscribe({ reviews.onNext(it) }, Timber::e))

        disposable.add(
            progressStatusProvider
                .addProgressObservable(
                    reviewSource
                        .map({ notification -> notification })))

        disposable.add(
            reviewSource
                .connect())
    }
}
