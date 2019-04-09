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
import io.reark.reark.data.utils.DataLayerUtils
import polanski.option.Option
import quickbeer.android.data.Validator
import quickbeer.android.data.actions.CountryActions
import quickbeer.android.data.onValidationError
import quickbeer.android.data.pojos.Country
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.CountryStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.data.validate
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
        Timber.v("get($countryId)")

        return countryStore.getOnce(countryId)
    }

    // BEERS IN COUNTRY

    override fun beers(
        countryId: Int,
        validator: Validator<Option<ItemList<String>>>
    ): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("beers($countryId)")

        val queryId = BeerSearchFetcher.getQueryId(BeersInCountryFetcher.NAME, countryId.toString())
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
                    serviceUri = BeersInCountryFetcher.NAME,
                    intParams = mapOf(BeersInCountryFetcher.COUNTRY_ID to countryId))
            }

        return createNotificationStream(statusStream, valueStream)
            .mergeWith(reloadTrigger)
    }
}
