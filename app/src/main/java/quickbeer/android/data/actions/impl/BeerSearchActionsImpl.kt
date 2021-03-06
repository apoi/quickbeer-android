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
import polanski.option.Option
import quickbeer.android.Constants
import quickbeer.android.data.Validator
import quickbeer.android.data.actions.BeerSearchActions
import quickbeer.android.data.onValidationError
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.data.validate
import quickbeer.android.network.fetchers.impl.BeerSearchFetcher
import quickbeer.android.utils.StringUtils
import quickbeer.android.utils.kotlin.filterToValue
import timber.log.Timber
import javax.inject.Inject

class BeerSearchActionsImpl @Inject constructor(
    context: Context,
    private val requestStatusStore: NetworkRequestStatusStore,
    private val beerListStore: BeerListStore
) : ApplicationDataLayer(context), BeerSearchActions {

    // SEARCH BEERS

    override fun searchQueries(): Single<List<String>> {
        Timber.v("searchQueries()")

        return beerListStore.getOnce()
            .map {
                it.mapNotNull { it.key }
                    .filter { !it.startsWith(Constants.META_QUERY_PREFIX) }
            }
    }

    override fun search(
        query: String,
        validator: Validator<Option<ItemList<String>>>
    ): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("search($query)")

        val normalized = StringUtils.normalize(query)
        val queryId = BeerSearchFetcher.getQueryId(BeerSearchFetcher.NAME, normalized)
        val uri = BeerSearchFetcher.getUniqueUri(queryId)

        val statusStream = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue() // No need to filter stale statuses?

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
                    serviceUri = BeerSearchFetcher.NAME,
                    stringParams = mapOf(BeerSearchFetcher.SEARCH to query))
            }

        return createNotificationStream(statusStream, valueStream)
            .mergeWith(reloadTrigger)
    }
}
