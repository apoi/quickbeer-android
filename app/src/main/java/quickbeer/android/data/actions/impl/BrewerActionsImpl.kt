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
import quickbeer.android.data.actions.BrewerActions
import quickbeer.android.data.onValidationError
import quickbeer.android.data.pojos.Brewer
import quickbeer.android.data.pojos.BrewerMetadata
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.BrewerMetadataStore
import quickbeer.android.data.stores.BrewerStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.data.validate
import quickbeer.android.network.fetchers.impl.BeerSearchFetcher
import quickbeer.android.network.fetchers.impl.BrewerBeersFetcher
import quickbeer.android.network.fetchers.impl.BrewerFetcher
import quickbeer.android.utils.kotlin.filterToValue
import quickbeer.android.utils.kotlin.valueOrError
import timber.log.Timber
import javax.inject.Inject

class BrewerActionsImpl @Inject constructor(
    context: Context,
    private val requestStatusStore: NetworkRequestStatusStore,
    private val beerListStore: BeerListStore,
    private val brewerStore: BrewerStore,
    private val brewerMetadataStore: BrewerMetadataStore
) : ApplicationDataLayer(context), BrewerActions {

    // GET BREWER DETAILS

    override operator fun get(
        brewerId: Int,
        validator: Validator<ZonedDateTime?>,
        valueToSatisfyInterface: Boolean // :(
    ): Observable<DataStreamNotification<Brewer>> {
        Timber.v("get($brewerId)")

        // Get and check metadata, and use or refresh existing beer
        // based on the metadata date
        return brewerMetadataStore.getOnce(brewerId)
            .valueOrError()
            .map { it.updated }
            .compose(validator.validate())
            .map<Validator<Option<Brewer>>> { Something() } // Data within limits
            .onErrorReturn { Reject() } // Validation failure, force refresh
            .flatMapObservable { get(brewerId, it) }
    }

    override fun get(
        brewerId: Int,
        validator: Validator<Option<Brewer>>
    ): Observable<DataStreamNotification<Brewer>> {
        Timber.v("get($brewerId)")

        val uri = BrewerFetcher.getUniqueUri(brewerId)

        val statusStream = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue()

        val valueStream = brewerStore
            .getOnceAndStream(brewerId)
            .filterToValue()

        // Trigger a fetch only if full details haven't been fetched
        val reloadTrigger = brewerStore
            .getOnce(brewerId)
            .validate(validator)
            .onValidationError {
                Timber.v("Fetching brewer data")
                createServiceRequest(
                    serviceUri = BrewerFetcher.NAME,
                    intParams = mapOf(BrewerFetcher.BREWER_ID to brewerId))

            }

        return createNotificationStream(statusStream, valueStream)
            .mergeWith(reloadTrigger)
            .distinctUntilChanged()
    }

    // BREWER'S BEERS

    override fun beers(
        brewerId: Int,
        validator: Validator<Option<ItemList<String>>>
    ): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("beers($brewerId)")

        val queryId = BeerSearchFetcher.getQueryId(BrewerBeersFetcher.NAME, brewerId.toString())
        val uri = BeerSearchFetcher.getUniqueUri(queryId)

        val statusStream = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue()

        val valueStream = beerListStore
            .getOnceAndStream(queryId)
            .filterToValue()

        // Trigger a fetch only if there was no cached result
        val reloadTrigger = beerListStore
            .getOnce(queryId)
            .validate(validator)
            .onValidationError {
                Timber.v("Search not cached, fetching")
                createServiceRequest(
                    serviceUri = BrewerBeersFetcher.NAME,
                    intParams = mapOf(BrewerBeersFetcher.BREWER_ID to brewerId))
            }

        return createNotificationStream(statusStream, valueStream)
            .mergeWith(reloadTrigger)
    }

    // ACCESS BREWER

    override fun access(brewerId: Int): Single<Boolean> {
        Timber.v("access($brewerId)")

        return brewerMetadataStore.put(BrewerMetadata.newAccess(brewerId))
    }
}
