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

class BrewerBeersFetcher(
    networkApi: NetworkApi,
    networkUtils: NetworkUtils,
    networkRequestStatus: Consumer<NetworkRequestStatus>,
    beerStore: BeerStore,
    beerListStore: BeerListStore
) : BeerSearchFetcher(networkApi, networkUtils, networkRequestStatus, beerStore, beerListStore, NAME) {

    override fun requiredParams() = listOf(BREWER_ID)

    override fun fetch(intent: Intent, listenerId: Int) {
        if (!validateParams(intent)) return

        val brewerId = intent.getIntExtra(BREWER_ID, 0)
        fetchBeerSearch(brewerId.toString(), listenerId)
    }

    override fun createNetworkObservable(brewerId: String): Single<List<Beer>> {
        return networkApi.getBrewerBeers(networkUtils.createRequestParams("b", brewerId))
    }

    companion object : FetcherCompanion {
        override val NAME = "__brewer_beers"
        const val BREWER_ID = "brewerId"
    }
}
