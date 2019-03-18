/**
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
package quickbeer.android.network.fetchers

import android.content.Intent
import android.net.Uri
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reark.reark.network.fetchers.FetcherBase
import io.reark.reark.pojo.NetworkRequestStatus
import io.reark.reark.utils.Preconditions.get
import quickbeer.android.data.pojos.Beer
import quickbeer.android.data.pojos.BeerMetadata
import quickbeer.android.data.stores.BeerMetadataStore
import quickbeer.android.data.stores.BeerStore
import quickbeer.android.network.NetworkApi
import quickbeer.android.network.RateBeerService
import quickbeer.android.network.utils.NetworkUtils
import timber.log.Timber

class BeerFetcher(
    private val networkApi: NetworkApi,
    private val networkUtils: NetworkUtils,
    networkRequestStatus: Consumer<NetworkRequestStatus>,
    private val beerStore: BeerStore,
    private val metadataStore: BeerMetadataStore
) : FetcherBase<Uri>(networkRequestStatus) {

    override fun fetch(intent: Intent, listenerId: Int) {
        if (!intent.hasExtra("id")) {
            Timber.e("Missing required fetch parameters!")
            return
        }

        val beerId = get(intent).getIntExtra("id", 0)
        val uri = getUniqueUri(beerId)

        addListener(beerId, listenerId)

        if (isOngoingRequest(beerId)) {
            Timber.d("Found an ongoing request for beer %s", beerId)
            return
        }

        Timber.d("fetchBeer(requestId=%s, hashCode=%s, uri=%s", beerId, uri.hashCode(), uri)
        addRequest(beerId, createRequest(beerId, uri))
    }

    private fun createRequest(beerId: Int, uri: String): Disposable {
        val requestParams = networkUtils.createRequestParams("bd", beerId.toString())

        return networkApi.getBeer(requestParams)
            .subscribeOn(Schedulers.io())
            .flatMap { beerStore.put(it) }
            .doOnSubscribe { startRequest(beerId, uri) }
            .doOnSuccess { completeRequest(beerId, uri, it) }
            .doOnError(doOnError(beerId, uri))
            .subscribe({ metadataStore.put(BeerMetadata.newUpdate(beerId)) },
                { Timber.w(it, "Error fetching beer %s", beerId) })
    }

    override fun getServiceUri(): Uri {
        return RateBeerService.BEER
    }

    companion object {
        fun getUniqueUri(id: Int): String {
            return Beer::class.java.toString() + "/" + id
        }
    }
}
