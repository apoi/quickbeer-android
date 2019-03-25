/*
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
package quickbeer.android.data.actions.impl

import android.content.Context
import io.reactivex.Observable
import io.reactivex.Single
import io.reark.reark.data.DataStreamNotification
import io.reark.reark.data.utils.DataLayerUtils
import polanski.option.Option
import quickbeer.android.data.actions.ReviewActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.pojos.Review
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.data.stores.ReviewStore
import quickbeer.android.network.fetchers.impl.BeerSearchFetcher
import quickbeer.android.network.fetchers.impl.ReviewsFetcher
import quickbeer.android.network.fetchers.impl.TicksFetcher
import quickbeer.android.utils.kotlin.filterToValue
import timber.log.Timber
import javax.inject.Inject

class ReviewActionsImpl @Inject constructor(
    context: Context,
    private val requestStatusStore: NetworkRequestStatusStore,
    private val beerListStore: BeerListStore,
    private val reviewStore: ReviewStore
) : ApplicationDataLayer(context), ReviewActions {

    // REVIEW

    override operator fun get(reviewId: Int): Observable<Option<Review>> {
        Timber.v("get(%s)", reviewId)

        // Reviews are never fetched one-by-one, only as a list of reviews. This method can only
        // return reviews from the local store, no fetching.
        return reviewStore.getOnceAndStream(reviewId)
    }

    // USER TICKS

    override fun getTicks(userId: String): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("getTicks($userId)")

        return getTicks(userId, { it.items.isEmpty() })
    }

    override fun fetchTicks(userId: String): Single<Boolean> {
        Timber.v("fetchTicks($userId)")

        return getTicks(userId, { true })
            .filter { it.isCompleted }
            .map { it.isCompletedWithSuccess }
            .firstOrError()
    }

    private fun getTicks(
        userId: String,
        needsReload: (ItemList<String>) -> Boolean
    ): Observable<DataStreamNotification<ItemList<String>>> {
        // Trigger a fetch only if there was no cached result
        val triggerFetchIfEmpty = beerListStore
            .getOnce(BeerSearchFetcher.getQueryId(TicksFetcher.NAME, userId))
            .toObservable()
            .filter { it.match({ needsReload(it) }, { true }) }
            .doOnNext { Timber.v("Search not cached, fetching") }
            .doOnNext { triggerTicksFetch(userId) }
            .flatMap { Observable.empty<DataStreamNotification<ItemList<String>>>() }

        return getTicksResultStream(userId)
            .mergeWith(triggerFetchIfEmpty)
    }

    private fun getTicksResultStream(userId: String): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("getTicksResultStream($userId)")

        val queryId = BeerSearchFetcher.getQueryId(TicksFetcher.NAME, userId)
        val uri = BeerSearchFetcher.getUniqueUri(queryId)

        val requestStatusObservable = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue() // No need to filter stale statuses?

        val beerSearchObservable = beerListStore
            .getOnceAndStream(queryId)
            .filterToValue()

        return DataLayerUtils.createDataStreamNotificationObservable(
            requestStatusObservable, beerSearchObservable)
    }

    private fun triggerTicksFetch(userId: String): Int {
        Timber.v("triggerTicksFetch($userId)")

        return createServiceRequest(
            serviceUri = TicksFetcher.NAME,
            stringParams = mapOf(TicksFetcher.USER_ID to userId))
    }

    // USER REVIEWS

    override fun getReviews(userId: String): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("getReviews($userId)")

        return getReviews(userId, { it.items.isEmpty() })
    }

    override fun fetchReviews(userId: String): Single<Boolean> {
        Timber.v("fetchReviews($userId)")

        return getReviews(userId, { true })
            .filter { it.isCompleted }
            .map { it.isCompletedWithSuccess }
            .firstOrError()
    }

    private fun getReviews(
        userId: String,
        needsReload: (ItemList<String>) -> Boolean
    ): Observable<DataStreamNotification<ItemList<String>>> {
        // Trigger a fetch only if there was no cached result
        val triggerFetchIfEmpty = beerListStore
            .getOnce(BeerSearchFetcher.getQueryId(ReviewsFetcher.NAME, userId))
            .toObservable()
            .filter { it.match({ needsReload(it) }, { true }) }
            .doOnNext { Timber.v("Search not cached, fetching") }
            .doOnNext { triggerReviewFetch(userId) }
            .flatMap { Observable.empty<DataStreamNotification<ItemList<String>>>() }

        return getReviewsResultStream(userId)
            .mergeWith(triggerFetchIfEmpty)
    }

    private fun getReviewsResultStream(userId: String): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("getReviewsResultStream($userId)")

        val queryId = BeerSearchFetcher.getQueryId(ReviewsFetcher.NAME, userId)
        val uri = BeerSearchFetcher.getUniqueUri(queryId)

        val requestStatusObservable = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue() // No need to filter stale statuses?

        val beerSearchObservable = beerListStore
            .getOnceAndStream(queryId)
            .filterToValue()

        return DataLayerUtils.createDataStreamNotificationObservable(
            requestStatusObservable, beerSearchObservable)
    }

    private fun triggerReviewFetch(userId: String): Int {
        Timber.v("triggerReviewFetch($userId)")

        return createServiceRequest(
            serviceUri = ReviewsFetcher.NAME,
            stringParams = mapOf(ReviewsFetcher.USER_ID to userId),
            intParams = mapOf(ReviewsFetcher.NUM_REVIEWS to 1))
    }
}
