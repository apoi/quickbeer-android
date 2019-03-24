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
import quickbeer.android.data.actions.BarcodeActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.network.NetworkService
import quickbeer.android.network.fetchers.impl.BarcodeSearchFetcher
import quickbeer.android.network.fetchers.impl.BeerSearchFetcher
import quickbeer.android.utils.kotlin.filterToValue
import quickbeer.android.utils.kotlin.isNoneOrEmpty
import timber.log.Timber
import javax.inject.Inject

class BarcodeActionsImpl @Inject constructor(
    context: Context,
    private val requestStatusStore: NetworkRequestStatusStore,
    private val beerListStore: BeerListStore
) : ApplicationDataLayer(context), BarcodeActions {

    // BARCODE SEARCH

    override operator fun get(barcode: String): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("getBarcodeSearch(%s)", barcode)

        // Trigger a fetch only if there was no cached result
        val triggerFetchIfEmpty = beerListStore
            .getOnce(BeerSearchFetcher.getQueryId(BarcodeSearchFetcher.NAME, barcode))
            .toObservable()
            .filter { it.isNoneOrEmpty() }
            .doOnNext { Timber.v("Search not cached, fetching") }
            .doOnNext { fetchBarcodeSearch(barcode) }
            .flatMap { Observable.empty<DataStreamNotification<ItemList<String>>>() }

        return getBarcodeSearchResultStream(barcode)
            .mergeWith(triggerFetchIfEmpty)
    }

    override fun fetch(barcode: String): Single<Boolean> {
        return Single.just(false)
    }

    private fun getBarcodeSearchResultStream(barcode: String): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("getBarcodeSearchResultStream(%s)", barcode)

        val queryId = BeerSearchFetcher.getQueryId(BarcodeSearchFetcher.NAME, barcode)
        val uri = BeerSearchFetcher.getUniqueUri(queryId)

        val requestStatusObservable = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue() // No need to filter stale statuses?

        val barcodeSearchObservable = beerListStore
            .getOnceAndStream(queryId)
            .filterToValue()

        return DataLayerUtils.createDataStreamNotificationObservable(
            requestStatusObservable, barcodeSearchObservable)
    }

    private fun fetchBarcodeSearch(barcode: String): Int {
        Timber.v("fetchBarcodeSearch(%s)", barcode)

        val listenerId = createListenerId()
        val intent = Intent(context, NetworkService::class.java).apply {
            putExtra(ServiceDataLayer.SERVICE_URI, BarcodeSearchFetcher.NAME)
            putExtra(ServiceDataLayer.LISTENER_ID, listenerId)
            putExtra(BarcodeSearchFetcher.BARCODE, barcode)
        }

        context.startService(intent)
        return listenerId
    }
}
