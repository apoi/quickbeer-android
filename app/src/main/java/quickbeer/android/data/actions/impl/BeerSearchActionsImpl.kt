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
import quickbeer.android.Constants
import quickbeer.android.data.actions.BeerSearchActions
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.network.NetworkService
import quickbeer.android.network.RateBeerService
import quickbeer.android.network.fetchers.BeerSearchFetcher
import quickbeer.android.rx.RxUtils
import quickbeer.android.utils.StringUtils
import timber.log.Timber
import javax.inject.Inject

class BeerSearchActionsImpl @Inject
constructor(context: Context,
            private val requestStatusStore: NetworkRequestStatusStore,
            private val beerListStore: BeerListStore)
    : ApplicationDataLayer(context), BeerSearchActions {

    //// SEARCH BEERS

    override fun searchQueries(): Single<List<String>> {
        Timber.v("searchQueries")

        return beerListStore.getOnce()
                .toObservable()
                .flatMap { Observable.fromIterable(it) }
                .map { it.key }
                .filter { !it.startsWith(Constants.META_QUERY_PREFIX) }
                .toList()
                .map { ArrayList<String>(it) }
    }

    override fun search(query: String): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("search")

        return triggerSearch(query, { it.items.isEmpty() })
    }

    override fun fetchSearch(query: String): Single<Boolean> {
        Timber.v("fetchSearch")

        return triggerSearch(query, { true })
                .filter { it.isCompleted }
                .map { it.isCompletedWithSuccess }
                .singleOrError()
    }

    private fun triggerSearch(query: String, needsReload: (ItemList<String>) -> Boolean): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("triggerSearch(%s)", query)

        val normalized = StringUtils.normalize(query)

        // Trigger a fetch only if there was no cached result
        val triggerFetchIfEmpty = beerListStore
                .getOnce(BeerSearchFetcher.getQueryId(RateBeerService.SEARCH, normalized))
                .toObservable()
                .filter { it.match({ needsReload(it) }, { true }) }
                .doOnNext { Timber.v("Search not cached, fetching") }
                .doOnNext { fetchBeerSearch(normalized) }
                .flatMap { Observable.empty<DataStreamNotification<ItemList<String>>>() }

        return getBeerSearchResultStream(normalized)
                .mergeWith(triggerFetchIfEmpty)
    }

    private fun getBeerSearchResultStream(query: String): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("getBeerSearchResultStream(%s)", query)

        val queryId = BeerSearchFetcher.getQueryId(RateBeerService.SEARCH, query)
        val uri = BeerSearchFetcher.getUniqueUri(queryId)

        val requestStatusObservable = requestStatusStore
                .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
                .compose { RxUtils.pickValue(it) } // No need to filter stale statuses?

        val beerSearchObservable = beerListStore
                .getOnceAndStream(queryId)
                .compose { RxUtils.pickValue(it) }

        return DataLayerUtils.createDataStreamNotificationObservable(
                requestStatusObservable, beerSearchObservable)
    }

    private fun fetchBeerSearch(query: String): Int {
        Timber.v("fetchBeerSearch(%s)", query)

        val listenerId = createListenerId()
        val intent = Intent(context, NetworkService::class.java).apply {
            putExtra("serviceUriString", RateBeerService.SEARCH.toString())
            putExtra("listenerId", listenerId)
            putExtra("searchString", query)
        }

        context.startService(intent)
        return listenerId
    }
}
