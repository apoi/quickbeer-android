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
import io.reactivex.Completable
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

    // BEER

    override operator fun get(beerId: Int): Observable<DataStreamNotification<Beer>> {
        Timber.v("get($beerId)")

        return getBeer(beerId, Beer::basicDataMissing)
    }

    override fun getDetails(beerId: Int): Observable<DataStreamNotification<Beer>> {
        Timber.v("getDetails($beerId)")

        return getBeer(beerId, Beer::detailedDataMissing)
    }

    private fun getBeer(beerId: Int, needsReload: (Beer) -> Boolean): Observable<DataStreamNotification<Beer>> {
        Timber.v("getBeer($beerId)")

        val uri = BeerFetcher.getUniqueUri(beerId)

        val statusStream = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue() // No need to filter stale statuses

        val valueStream = beerStore.getOnceAndStream(beerId)
            .filterToValue()

        // Trigger a fetch only if full details haven't been fetched
        val reloadTrigger = beerStore.getOnce(beerId)
            .filter { it.match({ needsReload(it) }, { true }) }
            .flatMapCompletable {
                Timber.v("Fetching beer data")
                fetch(beerId)
            }

        return DataLayerUtils.createDataStreamNotificationObservable(statusStream, valueStream)
            .mergeWith(reloadTrigger)
            .distinctUntilChanged()
    }

    override fun fetch(beerId: Int): Completable {
        Timber.v("fetch($beerId)")

        return Completable.fromCallable {
            createServiceRequest(
                serviceUri = BeerFetcher.NAME,
                intParams = mapOf(BeerFetcher.BEER_ID to beerId))
        }
    }

    // ACCESS

    override fun access(beerId: Int): Single<Boolean> {
        Timber.v("access($beerId)")

        return beerMetadataStore.put(BeerMetadata.newAccess(beerId))
    }

    // REVIEWS

    override fun getReviews(beerId: Int): Observable<DataStreamNotification<ItemList<Int>>> {
        Timber.v("getReviews($beerId)")

        val uri = ReviewFetcher.getUniqueUri(beerId)

        val statusStream = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue() // No need to filter stale statuses?

        val valueStream = reviewListStore
            .getOnceAndStream(beerId)
            .filterToValue()

        // Trigger a fetch only if there was no cached result
        val reloadTrigger = reviewListStore
            .getOnce(beerId)
            .filter { it.isNoneOrEmpty() }
            .flatMapCompletable {
                Timber.v("Reviews not cached, fetching")
                fetchReviews(beerId, 1)
            }

        return DataLayerUtils.createDataStreamNotificationObservable(statusStream, valueStream)
            .mergeWith(reloadTrigger)
    }

    override fun fetchReviews(beerId: Int, page: Int): Completable {
        Timber.v("fetchReviews($beerId, $page)")

        return Completable.fromAction {
            createServiceRequest(
                serviceUri = ReviewFetcher.NAME,
                intParams = mapOf(
                    ReviewFetcher.BEER_ID to beerId,
                    ReviewFetcher.PAGE to page
                ))
        }
    }

    // TICK

    override fun tick(beerId: Int, rating: Int): Observable<DataStreamNotification<Void>> {
        Timber.v("tick($beerId, $rating)")

        val uri = TickBeerFetcher.getUniqueUri(beerId, rating)

        val listenerId = createServiceRequest(
            serviceUri = TickBeerFetcher.NAME,
            intParams = mapOf(
                TickBeerFetcher.BEER_ID to beerId,
                TickBeerFetcher.RATING to rating
            ))

        val statusStream = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue()
            .filter { it.forListener(listenerId) }

        return DataLayerUtils.createDataStreamNotificationObservable(
            statusStream, Observable.never())
    }
}
