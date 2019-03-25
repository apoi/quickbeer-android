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
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reark.reark.pojo.NetworkRequestStatus
import io.reark.reark.utils.Preconditions.get
import org.threeten.bp.ZonedDateTime
import quickbeer.android.Constants
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.pojos.Review
import quickbeer.android.data.stores.ReviewListStore
import quickbeer.android.data.stores.ReviewStore
import quickbeer.android.network.NetworkApi
import quickbeer.android.network.fetchers.CheckingFetcher
import quickbeer.android.network.utils.NetworkUtils
import timber.log.Timber

class ReviewsFetcher(
    private val networkApi: NetworkApi,
    private val networkUtils: NetworkUtils,
    networkRequestStatus: Consumer<NetworkRequestStatus>,
    private val reviewStore: ReviewStore,
    private val reviewListStore: ReviewListStore
) : CheckingFetcher(networkRequestStatus, NAME) {

    override fun required() = listOf(USER_ID, NUM_REVIEWS)

    override fun fetch(intent: Intent, listenerId: Int) {
        if (!validateParams(intent)) return

        val userId = get(intent).getStringExtra(USER_ID)
        val numReviews = get(intent).getIntExtra(NUM_REVIEWS, 0)

        fetchReviewedBeers(userId, numReviews, listenerId)
    }

    private fun fetchReviewedBeers(userId: String, numReviews: Int, listenerId: Int) {
        Timber.d("fetchReviewedBeers($numReviews)")

        val uri = getUniqueUri(userId)
        val queryId = userId.hashCode()
        val requestId = uri.hashCode()

        addListener(requestId, listenerId)

        if (isOngoingRequest(requestId)) {
            Timber.d("Found an ongoing request for search $queryId")
            return
        }

        getReviews(1, numReviews)
            .map { it.sortedWith(compareByDescending(Review::timeUpdated).thenBy(Review::id)) }
            .map { it.mapNotNull(Review::countryID) }
            .map { reviewIds -> ItemList.create(queryId, reviewIds, ZonedDateTime.now()) }
            .flatMap { reviewListStore.put(it) }
            .doOnSubscribe { startRequest(requestId, uri) }
            .doOnSuccess { updated -> completeRequest(requestId, uri, updated!!) }
            .doOnError(doOnError(requestId, uri))
            .subscribe({}, { Timber.w(it, "Error fetching reviews for user $userId") })
            .also { addRequest(requestId, it) }
    }

    private fun getReviews(page: Int, numReviews: Int): Single<List<Review>> {
        return createNetworkObservable(page.toString())
            .subscribeOn(Schedulers.io())
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapSingle { review -> reviewStore.put(review).map { review } }
            .toList()
            .zipWith(
                chooseNextAction(page + 1, numReviews),
                BiFunction { list1, list2 -> list1 + list2 }
            )
    }

    private fun chooseNextAction(page: Int, numReviews: Int): Single<List<Review>> {
        return if (page <= Math.ceil((numReviews / Constants.USER_REVIEWS_PER_PAGE.toFloat()).toDouble())) {
            getReviews(page, numReviews)
        } else {
            Single.just(emptyList())
        }
    }

    private fun createNetworkObservable(page: String): Single<List<Review>> {
        val params = networkUtils.createRequestParams("m", "BR").apply {
            this["p"] = page
        }

        return networkApi.getUserReviews(params)
    }

    companion object {
        const val NAME = "__user_reviews"
        const val USER_ID = "userId"
        const val NUM_REVIEWS = "numReviews"

        fun getUniqueUri(userId: String): String {
            return ItemList::class.java.toString() + "/reviews/" + userId
        }
    }
}
