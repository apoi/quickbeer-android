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
import quickbeer.android.data.actions.StyleActions
import quickbeer.android.data.onValidationError
import quickbeer.android.data.pojos.BeerStyle
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.BeerStyleStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.data.validate
import quickbeer.android.network.fetchers.impl.BeerSearchFetcher
import quickbeer.android.network.fetchers.impl.BeersInStyleFetcher
import quickbeer.android.utils.kotlin.filterToValue
import timber.log.Timber
import javax.inject.Inject

class StyleActionsImpl @Inject constructor(
    context: Context,
    private val requestStatusStore: NetworkRequestStatusStore,
    private val beerListStore: BeerListStore,
    private val beerStyleStore: BeerStyleStore
) : ApplicationDataLayer(context), StyleActions {

    // STYLES

    override operator fun get(styleId: Int): Single<Option<BeerStyle>> {
        Timber.v("get($styleId)")

        return beerStyleStore.getOnce(styleId)
    }

    // BEERS IN STYLE

    override fun beers(
        styleId: Int,
        validator: Validator<Option<ItemList<String>>>
    ): Observable<DataStreamNotification<ItemList<String>>> {
        Timber.v("beers($styleId)")

        val queryId = BeerSearchFetcher.getQueryId(BeersInStyleFetcher.NAME, styleId.toString())
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
                    serviceUri = BeersInStyleFetcher.NAME,
                    intParams = mapOf(BeersInStyleFetcher.STYLE_ID to styleId))
            }

        return DataLayerUtils.createDataStreamNotificationObservable(statusStream, valueStream)
            .mergeWith(reloadTrigger)
    }
}
