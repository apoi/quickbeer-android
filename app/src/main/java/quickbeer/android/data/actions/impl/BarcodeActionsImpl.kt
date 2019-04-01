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
import quickbeer.android.data.actions.BarcodeActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
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
        Timber.v("get($barcode)")

        val queryId = BeerSearchFetcher.getQueryId(BarcodeSearchFetcher.NAME, barcode)
        val uri = BeerSearchFetcher.getUniqueUri(queryId)

        val statusStream = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue() // No need to filter stale statuses

        val valueStream = beerListStore
            .getOnceAndStream(queryId)
            .filterToValue()

        // Trigger a fetch only if there was no cached result
        val reloadTrigger = beerListStore
            .getOnce(BeerSearchFetcher.getQueryId(BarcodeSearchFetcher.NAME, barcode))
            .filter { it.isNoneOrEmpty() }
            .flatMapCompletable {
                Timber.v("Search not cached, fetching")
                fetch(barcode)
            }

        return DataLayerUtils.createDataStreamNotificationObservable(statusStream, valueStream)
            .mergeWith(reloadTrigger)
    }

    override fun fetch(barcode: String): Completable {
        Timber.v("fetch($barcode)")

        return Completable.fromCallable {
            createServiceRequest(
                serviceUri = BarcodeSearchFetcher.NAME,
                stringParams = mapOf(BarcodeSearchFetcher.BARCODE to barcode))
        }
    }
}
