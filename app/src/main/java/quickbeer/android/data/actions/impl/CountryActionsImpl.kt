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
import polanski.option.Option
import quickbeer.android.data.access.ServiceDataLayer
import quickbeer.android.data.actions.CountryActions
import quickbeer.android.data.pojos.Country
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.CountryStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.network.NetworkService
import quickbeer.android.network.RateBeerService
import quickbeer.android.network.fetchers.impl.BeerSearchFetcher
import quickbeer.android.network.fetchers.impl.BeersInCountryFetcher
import quickbeer.android.utils.kotlin.filterToValue
import timber.log.Timber
import javax.inject.Inject

class CountryActionsImpl @Inject constructor(
    context: Context,
    private val requestStatusStore: NetworkRequestStatusStore,
    private val beerListStore: BeerListStore,
    private val countryStore: CountryStore
) : ApplicationDataLayer(context), CountryActions {

    // COUNTRIES

    override operator fun get(countryId: Int): Single<Option<Country>> {
        Timber.v("get(%s)", countryId)

        return countryStore.getOnce(countryId)
    }

    // BEERS IN COUNTRY

    override fun beers(countryId: Int): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("beers(%s)", countryId)

        return triggerGetBeers(countryId, { it.items.isEmpty() })
    }

    override fun fetchBeers(countryId: Int): Single<Boolean> {
        Timber.v("fetchBeers(%s)", countryId)

        return triggerGetBeers(countryId, { true })
            .filter { it.isCompleted }
            .map { it.isCompletedWithSuccess }
            .firstOrError()
    }

    private fun triggerGetBeers(
        countryId: Int,
        needsReload: (ItemList<String>) -> Boolean
    ): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("triggerGetBeers(%s)", countryId)

        // Trigger a fetch only if there was no cached result
        val triggerFetchIfEmpty = beerListStore
            .getOnce(BeerSearchFetcher.getQueryId(BeersInCountryFetcher.NAME, countryId.toString()))
            .toObservable()
            .filter { it.match({ needsReload(it) }, { true }) }
            .doOnNext { Timber.v("Search not cached, fetching") }
            .doOnNext { fetchBeersInCountry(countryId) }
            .flatMap { Observable.empty<DataStreamNotification<ItemList<String>>>() }

        return getBeersInCountryResultStream(countryId)
            .mergeWith(triggerFetchIfEmpty)
    }

    private fun getBeersInCountryResultStream(countryId: Int): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("getBeersInCountryResultStream(%s)", countryId)

        val queryId = BeerSearchFetcher.getQueryId(BeersInCountryFetcher.NAME, countryId.toString())
        val uri = BeerSearchFetcher.getUniqueUri(queryId)

        val requestStatusObservable = requestStatusStore
            .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
            .filterToValue() // No need to filter stale statuses?

        val beerSearchObservable = beerListStore
            .getOnceAndStream(queryId)
            .filterToValue()

        return DataLayerUtils.createDataStreamNotificationObservable(
            requestStatusObservable, beerSearchObservable)
    }

    private fun fetchBeersInCountry(countryId: Int): Int {
        Timber.v("fetchBeersInCountry")

        val listenerId = createListenerId()
        val intent = Intent(context, NetworkService::class.java).apply {
            putExtra(ServiceDataLayer.SERVICE_URI, BeersInCountryFetcher.NAME)
            putExtra(ServiceDataLayer.LISTENER_ID, listenerId)
            putExtra(BeersInCountryFetcher.COUNTRY_ID, countryId)
        }

        context.startService(intent)
        return listenerId
    }
}
