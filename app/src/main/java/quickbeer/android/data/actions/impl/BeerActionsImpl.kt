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
import org.threeten.bp.ZonedDateTime
import polanski.option.Option
import quickbeer.android.data.Reject
import quickbeer.android.data.Something
import quickbeer.android.data.Validator
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.onValidationError
import quickbeer.android.data.pojos.Beer
import quickbeer.android.data.pojos.BeerMetadata
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerMetadataStore
import quickbeer.android.data.stores.BeerStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.data.stores.ReviewListStore
import quickbeer.android.data.validate
import quickbeer.android.network.fetchers.impl.BeerFetcher
import quickbeer.android.network.fetchers.impl.ReviewFetcher
import quickbeer.android.network.fetchers.impl.TickBeerFetcher
import quickbeer.android.utils.kotlin.filterToValue
import quickbeer.android.utils.kotlin.valueOrError
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

    override fun get(
        beerId: Int,
        validator: Validator<ZonedDateTime?>,
        valueToSatisfyInterface: Boolean // :(
    ): Observable<DataStreamNotification<Beer>> {
        Timber.v("get($beerId)")

        // Get and check metadata, and use or refresh existing beer
        // based on the metadata date
        return beerMetadataStore.getOnce(beerId)
            .valueOrError()
            .map { it.updated }
            .compose(validator.validate())
            .map<Validator<Option<Beer>>> { Something() } // Data within limits
            .onErrorReturn { Reject() } // Validation failure, force refresh
            .flatMapObservable { get(beerId, it) }
    }

    override operator fun get(
        beerId: Int,
        validator: Validator<Option<Beer>>
    ): Observable<DataStreamNotification<Beer>> {
        Timber.v("get($beerId)")

        val uri = BeerFetcher.getUniqueUri(beerId)

        val statusStream = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue() // No need to filter stale statuses

        val valueStream = beerStore
            .getOnceAndStream(beerId)
            .filterToValue()

        // Trigger a fetch only if full details haven't been fetched
        val reloadTrigger = beerStore
            .getOnce(beerId)
            .validate(validator)
            .onValidationError {
                Timber.v("Fetching beer data")
                createServiceRequest(
                    serviceUri = BeerFetcher.NAME,
                    intParams = mapOf(BeerFetcher.BEER_ID to beerId))
            }

        return createNotificationStream(statusStream, valueStream)
            .mergeWith(reloadTrigger)
            .distinctUntilChanged()
    }

    // ACCESS

    override fun access(beerId: Int): Single<Boolean> {
        Timber.v("access($beerId)")

        return beerMetadataStore.put(BeerMetadata.newAccess(beerId))
    }

    // REVIEWS

    override fun getReviews(
        beerId: Int,
        validator: Validator<Option<ItemList<Int>>>
    ): Observable<DataStreamNotification<ItemList<Int>>> {
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
            .validate(validator)
            .onValidationError(
                reviewListStore
                    .delete(beerId) // Reviews accumulate, so on reload list is cleared
                    .ignoreElement()
                    .doOnComplete {
                        Timber.v("Reviews not cached, fetching")
                        createServiceRequest(
                            serviceUri = ReviewFetcher.NAME,
                            intParams = mapOf(
                                ReviewFetcher.BEER_ID to beerId,
                                ReviewFetcher.PAGE to 1
                            ))
                    }
            )

        return createNotificationStream(statusStream, valueStream)
            .mergeWith(reloadTrigger)
    }

    override fun getReviews(beerId: Int, page: Int) {
        Timber.v("getReviews($beerId, $page)")

        createServiceRequest(
            serviceUri = ReviewFetcher.NAME,
            intParams = mapOf(
                ReviewFetcher.BEER_ID to beerId,
                ReviewFetcher.PAGE to page
            ))
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

        return createNotificationStream(statusStream, Observable.never())
    }
}
