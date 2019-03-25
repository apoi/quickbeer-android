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
import android.content.Intent
import io.reactivex.Observable
import io.reactivex.Single
import io.reark.reark.data.DataStreamNotification
import io.reark.reark.data.utils.DataLayerUtils
import quickbeer.android.data.access.ServiceDataLayer
import quickbeer.android.data.actions.BeerListActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.BeerMetadataStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.network.NetworkService
import quickbeer.android.network.fetchers.impl.BarcodeSearchFetcher
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

    // No need to filter stale statuses?
    private fun getTopBeersResultStream(): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("getTopBeersResultStream()")

        val queryId = BeerSearchFetcher.getQueryId(TopBeersFetcher.NAME)
        val uri = BeerSearchFetcher.getUniqueUri(queryId)

        val requestStatusObservable = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue()

        val beerSearchObservable = beerListStore.getOnceAndStream(queryId)
            .filterToValue()

        return DataLayerUtils.createDataStreamNotificationObservable(
            requestStatusObservable, beerSearchObservable)
    }

    // ACCESSED BEERS

    override fun accessed(): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("accessed()")

        return beerMetadataStore.accessedIdsOnce
            .map { ids -> ItemList<String>(null, ids, null) }
            .map { DataStreamNotification.onNext(it) }
    }

    // TOP BEERS

    override fun topBeers(): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("topBeers()")

        return triggerGet({ list -> list.items.isEmpty() })
    }

    override fun fetchTopBeers(): Single<Boolean> {
        Timber.v("fetchTopBeers()")

        return triggerGet({ true })
            .filter { it.isCompleted }
            .map { it.isCompletedWithSuccess }
            .firstOrError()
    }

    private fun triggerGet(needsReload: (ItemList<String>) -> Boolean): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("triggerGetBeers()")

        // Trigger a fetch only if there was no cached result
        val triggerFetchIfEmpty = beerListStore.getOnce(BeerSearchFetcher.getQueryId(TopBeersFetcher.NAME))
            .toObservable()
            .filter { it.match({ needsReload(it) }, { true }) }
            .doOnNext { Timber.v("Search not cached, fetching") }
            .doOnNext { triggerFetch() }
            .flatMap { Observable.empty<DataStreamNotification<ItemList<String>>>() }

        return getTopBeersResultStream()
            .mergeWith(triggerFetchIfEmpty)
    }

    private fun triggerFetch(): Int {
        Timber.v("triggerFetch()")

        return createServiceRequest(serviceUri = TopBeersFetcher.NAME)
    }
}
