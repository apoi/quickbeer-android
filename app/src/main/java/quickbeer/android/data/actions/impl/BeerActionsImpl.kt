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
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.pojos.Beer
import quickbeer.android.data.pojos.BeerMetadata
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerMetadataStore
import quickbeer.android.data.stores.BeerStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.data.stores.ReviewListStore
import quickbeer.android.network.fetchers.impl.BeerFetcher
import quickbeer.android.network.fetchers.impl.ReviewFetcher
import quickbeer.android.network.fetchers.impl.TickBeerFetcher
import quickbeer.android.utils.kotlin.filterToValue
import quickbeer.android.utils.kotlin.isNoneOrEmpty
import timber.log.Timber
import javax.inject.Inject

class BeerActionsImpl @Inject constructor(
    context: Context,
    private val requestStatusStore: NetworkRequestStatusStore,
    private val beerStore: BeerStore,
    private val beerMetadataStore: BeerMetadataStore,
    private val reviewListStore: ReviewListStore
) : ApplicationDataLayer(context), BeerActions {

    // API

    override operator fun get(beerId: Int): Observable<DataStreamNotification<Beer>> {
        return getBeer(beerId, { it.basicDataMissing() })
    }

    override fun getDetails(beerId: Int): Observable<DataStreamNotification<Beer>> {
        return getBeer(beerId, { it.detailedDataMissing() })
    }

    override fun fetch(beerId: Int): Single<Boolean> {
        return getBeer(beerId, { true })
            .filter { it.isCompleted }
            .map { it.isCompletedWithSuccess }
            .firstOrError()
    }

    override fun access(beerId: Int) {
        Timber.v("access($beerId)")

        beerMetadataStore.put(BeerMetadata.newAccess(beerId))
    }

    override fun getReviews(beerId: Int): Observable<DataStreamNotification<ItemList<Int>>> {
        Timber.v("getReviews($beerId)")

        // Trigger a fetch only if there was no cached result
        val triggerFetchIfEmpty = reviewListStore.getOnce(beerId)
            .toObservable()
            .filter { it.isNoneOrEmpty() }
            .doOnNext { Timber.v("Reviews not cached, fetching") }
            .doOnNext { fetchReviews(beerId, 1) }
            .flatMap { Observable.empty<DataStreamNotification<ItemList<Int>>>() }

        return getReviewsResultStream(beerId)
            .mergeWith(triggerFetchIfEmpty)
    }

    override fun fetchReviews(beerId: Int, page: Int) {
        triggerReviewsFetch(beerId, page)
    }

    override fun tick(beerId: Int, rating: Int): Observable<DataStreamNotification<Void>> {
        Timber.v("tick($beerId, $rating)")

        val listenerId = createServiceRequest(
            serviceUri = TickBeerFetcher.NAME,
            intParams = mapOf(
                TickBeerFetcher.BEER_ID to beerId,
                TickBeerFetcher.RATING to rating
            ))

        val uri = TickBeerFetcher.getUniqueUri(beerId, rating)
        val requestStatusObservable = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue()
            .filter { it.forListener(listenerId) }

        return DataLayerUtils.createDataStreamNotificationObservable(
            requestStatusObservable, Observable.never())
    }

    // BEER

    private fun getBeer(beerId: Int, needsReload: (Beer) -> Boolean): Observable<DataStreamNotification<Beer>> {
        Timber.v("getBeer($beerId)")

        // Trigger a fetch only if full details haven't been fetched
        val triggerFetchIfEmpty = beerStore.getOnce(beerId)
            .toObservable()
            .filter { it.match({ needsReload(it) }, { true }) }
            .doOnNext { Timber.v("Fetching beer data") }
            .doOnNext { triggerBeerFetch(beerId) }
            .flatMap { Observable.empty<DataStreamNotification<Beer>>() }

        return getBeerResultStream(beerId)
            .mergeWith(triggerFetchIfEmpty)
            .distinctUntilChanged()
    }

    private fun getBeerResultStream(beerId: Int): Observable<DataStreamNotification<Beer>> {
        Timber.v("getBeerResultStream($beerId)")

        val uri = BeerFetcher.getUniqueUri(beerId)

        val requestStatusObservable = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue() // No need to filter stale statuses

        val beerObservable = beerStore.getOnceAndStream(beerId)
            .filterToValue()

        return DataLayerUtils.createDataStreamNotificationObservable(
            requestStatusObservable, beerObservable)
    }

    private fun triggerBeerFetch(beerId: Int): Int {
        Timber.v("triggerBeerFetch($beerId)")

        return createServiceRequest(
            serviceUri = BeerFetcher.NAME,
            intParams = mapOf(BeerFetcher.BEER_ID to beerId))
    }

    // GET REVIEWS

    private fun getReviews(
        beerId: Int,
        needsReload: (ItemList<Int>) -> Boolean
    ): Observable<DataStreamNotification<ItemList<Int>>> {
        Timber.v("getReviews($beerId)")

        // Trigger a fetch only if there was no cached result
        val triggerFetchIfEmpty = reviewListStore.getOnce(beerId)
            .toObservable()
            .filter { it.match({ needsReload(it) }, { true }) }
            .doOnNext { Timber.v("Reviews not cached, fetching") }
            .doOnNext { triggerReviewsFetch(beerId, 1) }
            .flatMap { Observable.empty<DataStreamNotification<ItemList<Int>>>() }

        return getReviewsResultStream(beerId)
            .mergeWith(triggerFetchIfEmpty)
    }

    private fun getReviewsResultStream(beerId: Int): Observable<DataStreamNotification<ItemList<Int>>> {
        Timber.v("getReviewsResultStream($beerId)")

        val uri = ReviewFetcher.getUniqueUri(beerId)

        val requestStatusObservable =
            requestStatusStore.getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
                .filterToValue() // No need to filter stale statuses?

        val reviewListObservable = reviewListStore.getOnceAndStream(beerId)
            .filterToValue()

        return DataLayerUtils.createDataStreamNotificationObservable(
            requestStatusObservable, reviewListObservable)
    }

    private fun triggerReviewsFetch(beerId: Int, page: Int): Int {
        Timber.v("triggerReviewsFetch($beerId)")

        return createServiceRequest(
            serviceUri = ReviewFetcher.NAME,
            intParams = mapOf(
                ReviewFetcher.BEER_ID to beerId,
                ReviewFetcher.PAGE to page
            ))
    }
}
