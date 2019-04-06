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
import io.reark.reark.data.DataStreamNotification
import io.reark.reark.data.utils.DataLayerUtils
import polanski.option.Option
import quickbeer.android.data.Validator
import quickbeer.android.data.actions.BeerListActions
import quickbeer.android.data.onValidationError
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.BeerMetadataStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.data.validate
import quickbeer.android.network.fetchers.impl.BeerSearchFetcher
import quickbeer.android.network.fetchers.impl.TopBeersFetcher
import quickbeer.android.utils.kotlin.filterToValue
import timber.log.Timber
import javax.inject.Inject

class BeerListActionsImpl @Inject constructor(
    context: Context,
    private val requestStatusStore: NetworkRequestStatusStore,
    private val beerListStore: BeerListStore,
    private val beerMetadataStore: BeerMetadataStore
) : ApplicationDataLayer(context), BeerListActions {

    // RECENT BEERS

    override fun recentBeers(): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("recentBeers()")

        return beerMetadataStore.getAccessedIdsOnce()
            .map { ItemList.create<String>(it) }
            .map { DataStreamNotification.onNext(it) }
    }

    // TOP BEERS

    override fun fetchTopBeers(): Completable {
        Timber.v("fetchTopBeers()")

        return Completable.fromAction {
            createServiceRequest(serviceUri = TopBeersFetcher.NAME)
        }
    }

    override fun topBeers(
        validator: Validator<Option<ItemList<String>>>
    ): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("topBeers()")

        val queryId = BeerSearchFetcher.getQueryId(TopBeersFetcher.NAME)
        val uri = BeerSearchFetcher.getUniqueUri(queryId)

        // No need to filter stale statuses: all of the statuses are pushed to the progress indicator,
        // and for that we want also statuses from refreshes with different listener ids.
        val statusStream = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue()

        val valueStream = beerListStore.getOnceAndStream(queryId)
            .filterToValue()

        // Trigger a fetch only if there was no cached result
        val reloadTrigger = beerListStore.getOnce(BeerSearchFetcher.getQueryId(TopBeersFetcher.NAME))
            .validate(validator)
            .onValidationError {
                Timber.v("Search not cached, fetching")
                createServiceRequest(serviceUri = TopBeersFetcher.NAME)
            }

        return DataLayerUtils.createDataStreamNotificationObservable(statusStream, valueStream)
            .mergeWith(reloadTrigger)
    }
}
