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
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reark.reark.pojo.NetworkRequestStatus
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.pojos.Review
import quickbeer.android.data.stores.ReviewListStore
import quickbeer.android.data.stores.ReviewStore
import quickbeer.android.network.NetworkApi
import quickbeer.android.network.fetchers.CommonFetcher
import quickbeer.android.network.utils.NetworkUtils
import timber.log.Timber
import kotlin.collections.set

class ReviewFetcher(
    private val networkApi: NetworkApi,
    private val networkUtils: NetworkUtils,
    networkRequestStatus: Consumer<NetworkRequestStatus>,
    private val reviewStore: ReviewStore,
    private val reviewListStore: ReviewListStore
) : CommonFetcher(networkRequestStatus, NAME) {

    override fun requiredParams() = listOf(BEER_ID)

    override fun fetch(intent: Intent, listenerId: Int) {
        if (!validateParams(intent)) return

        val beerId = intent.getIntExtra(BEER_ID, 0)
        val page = intent.getIntExtra(PAGE, 1)
        val uri = getUniqueUri(beerId)

        addListener(beerId, listenerId)

        if (isOngoingRequest(beerId)) {
            Timber.d("Found an ongoing request for reviews for beer $beerId")
            return
        }

        Timber.d("fetchReviews($beerId, $page)")

        createNetworkObservable(beerId, page)
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapSingle { review -> reviewStore.put(review).map { review } }
            .toList()
            .map { it.sortedWith(compareByDescending(Review::timeEntered).thenBy(Review::id)) }
            .map { it.map(Review::id) }
            .map { reviewIds -> ItemList.create(beerId, reviewIds) }
            .flatMap { reviewListStore.put(it) }
            .compose(addFetcherTracking(beerId, uri))
            .subscribe({}, { Timber.w(it, "Error fetching reviews for beer $beerId") })
            .also { addRequest(beerId, it) }
    }

    private fun createNetworkObservable(beerId: Int, page: Int): Single<List<Review>> {
        val params = networkUtils.createRequestParams("bid", beerId.toString()).apply {
            this["p"] = page.toString()
        }

        return networkApi.getReviews(params)
    }

    companion object : FetcherCompanion {
        override val NAME = "__reviews"
        const val BEER_ID = "beerId"
        const val PAGE = "page"
    }
}
