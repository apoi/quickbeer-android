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
import io.reactivex.schedulers.Schedulers
import io.reark.reark.pojo.NetworkRequestStatus
import io.reark.reark.utils.Preconditions.get
import quickbeer.android.data.pojos.Brewer
import quickbeer.android.data.pojos.BrewerMetadata
import quickbeer.android.data.stores.BrewerMetadataStore
import quickbeer.android.data.stores.BrewerStore
import quickbeer.android.network.NetworkApi
import quickbeer.android.network.fetchers.CheckingFetcher
import quickbeer.android.network.utils.NetworkUtils
import timber.log.Timber

class BrewerFetcher(
    private val networkApi: NetworkApi,
    private val networkUtils: NetworkUtils,
    networkRequestStatus: Consumer<NetworkRequestStatus>,
    private val brewerStore: BrewerStore,
    private val metadataStore: BrewerMetadataStore
) : CheckingFetcher(networkRequestStatus, NAME) {

    override fun required() = listOf(BREWER_ID)

    override fun fetch(intent: Intent, listenerId: Int) {
        if (!validateParams(intent)) return

        val brewerId = get(intent).getIntExtra(BREWER_ID, 0)
        val uri = getUniqueUri(brewerId)

        addListener(brewerId, listenerId)

        if (isOngoingRequest(brewerId)) {
            Timber.d("Found an ongoing request for brewer $brewerId")
            return
        }

        Timber.d("fetchBrewer($brewerId)")

        createNetworkObservable(brewerId)
            .subscribeOn(Schedulers.io())
            .flatMap { brewerStore.put(it) }
            .doOnSubscribe { startRequest(brewerId, uri) }
            .doOnSuccess { updated -> completeRequest(brewerId, uri, updated) }
            .doOnError(doOnError(brewerId, uri))
            .subscribe(
                { metadataStore.put(BrewerMetadata.newUpdate(brewerId)) },
                { error -> Timber.w(error, "Error fetching brewer $brewerId") })
            .also { addRequest(brewerId, it) }
    }

    private fun createNetworkObservable(brewerId: Int): Single<Brewer> {
        return networkApi.getBrewer(networkUtils.createRequestParams("b", brewerId.toString()))
    }

    companion object {
        const val NAME = "__brewer"
        const val BREWER_ID = "brewerId"

        fun getUniqueUri(id: Int): String {
            return Brewer::class.java.toString() + "/" + id
        }
    }
}
