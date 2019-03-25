/*
 * This file is part of QuickBeer.
 * Copyright (C) 2019 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.network.fetchers.impl

import android.content.Intent
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reark.reark.pojo.NetworkRequestStatus
import quickbeer.android.data.pojos.Beer
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.BeerStore
import quickbeer.android.network.NetworkApi
import quickbeer.android.network.utils.NetworkUtils

class BarcodeSearchFetcher(
    networkApi: NetworkApi,
    networkUtils: NetworkUtils,
    requestStatus: Consumer<NetworkRequestStatus>,
    beerStore: BeerStore,
    beerListStore: BeerListStore
) : BeerSearchFetcher(networkApi, networkUtils, requestStatus, beerStore, beerListStore, NAME) {

    override fun requiredParams() = listOf(BARCODE)

    override fun fetch(intent: Intent, listenerId: Int) {
        if (!validateParams(intent)) return

        val barcode = intent.getStringExtra(BARCODE)
        fetchBeerSearch(barcode, listenerId)
    }

    override fun createNetworkObservable(barcode: String): Single<List<Beer>> {
        return networkApi.barcode(networkUtils.createRequestParams("upc", barcode))
    }

    companion object : FetcherCompanion {
        override val NAME = "__barcode"
        const val BARCODE = "barcode"
    }
}
