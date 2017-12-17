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
import android.support.annotation.IntRange
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reark.reark.network.fetchers.FetcherBase
import io.reark.reark.pojo.NetworkRequestStatus
import io.reark.reark.utils.Preconditions.checkNotNull
import okhttp3.ResponseBody
import org.threeten.bp.ZonedDateTime
import quickbeer.android.Constants
import quickbeer.android.data.pojos.Beer
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.BeerStore
import quickbeer.android.data.stores.UserStore
import quickbeer.android.network.NetworkApi
import quickbeer.android.network.RateBeerService
import quickbeer.android.network.fetchers.actions.LoginAndRetry
import quickbeer.android.network.utils.NetworkUtils
import quickbeer.android.utils.ValueUtils
import quickbeer.android.utils.kotlin.valueOrError
import timber.log.Timber
import java.util.*

class TickBeerFetcher(val networkApi: NetworkApi,
                      val networkUtils: NetworkUtils,
                      networkRequestStatus: Consumer<NetworkRequestStatus>,
                      val beerStore: BeerStore,
                      val beerListStore: BeerListStore,
                      val userStore: UserStore) : FetcherBase<Uri>(networkRequestStatus) {

    override fun fetch(intent: Intent, listenerId: Int) {
        checkNotNull(intent)

        if (!intent.hasExtra("beerId") || !intent.hasExtra("rating")) {
            Timber.e("Missing required fetch parameters!")
            return
        }

        val beerId = intent.getIntExtra("beerId", 0)
        val rating = intent.getIntExtra("rating", 0)
        val uri = getUniqueUri(beerId, rating)
        val requestId = uri.hashCode()

        addListener(requestId, listenerId)

        if (isOngoingRequest(requestId)) {
            Timber.d("Found an ongoing request for beer tick")
            return
        }

        Timber.d("Tick rating of %s for beer %s", rating, beerId)

        val disposable = userId
                .flatMap { createNetworkObservable(beerId, rating, it) }
                .flatMap { beerStore.getOnce(beerId) }
                .compose { it.valueOrError() }
                .map { it.copy(tickValue = rating, tickDate = ZonedDateTime.now()) }
                .doOnSuccess { beerStore.put(it) }
                .zipWith(tickedBeers, BiFunction<Beer, List<Int>, List<Int>> { beer, ticks -> appendTick(beer, ticks) })
                .zipWith(queryId, BiFunction<List<Int>, String, ItemList<String>> { ticks, ticksQueryId -> ItemList.create(ticksQueryId, ticks, ZonedDateTime.now()) })
                .doOnSubscribe { startRequest(requestId, uri) }
                .doOnSuccess { completeRequest(requestId, uri, false) }
                .doOnError(doOnError(requestId, uri))
                .subscribe({ beerListStore.put(it) }, { Timber.w(it, "Error ticking beer") })

        addRequest(requestId, disposable)
    }

    private val userId: Single<Int>
        get() = userStore.getOnce(Constants.DEFAULT_USER_ID)
                .subscribeOn(Schedulers.io())
                .compose { it.valueOrError() }
                .map { it.id }

    private val tickedBeers: Single<List<Int>>
        get() = queryId.flatMap { beerListStore.getOnce(it) }
                .map { it.match({ it.items }, { emptyList() }) }

    private val queryId: Single<String>
        get() = userId
                .map { it.toString() }
                .map { BeerSearchFetcher.getQueryId(RateBeerService.USER_TICKS, it) }

    private fun createNetworkObservable(beerId: Int, @IntRange(from = 0, to = 5) rating: Int, userId: Int): Single<Boolean> {
        val requestParams = networkUtils.createRequestParams("b", beerId.toString())
        requestParams.put("u", userId.toString())

        if (rating == 0) {
            requestParams.put("m", "3")
        } else {
            requestParams.put("m", "2")
            requestParams.put("l", rating.toString())
        }

        return networkApi.tickBeer(requestParams)
                .flatMap { requestSuccessful(it) }
                .retryWhen(LoginAndRetry(networkApi, userStore))
    }

    override fun getServiceUri(): Uri {
        return RateBeerService.TICK_BEER
    }

    companion object {

        private val SUCCESS_RESPONSES = arrayOf("added", "updated", "removed", "deleted")

        private fun appendTick(beer: Beer, ticks: List<Int>): List<Int> {
            return ticks.toMutableList()
                    .apply {
                        remove(beer.id)
                        if (ValueUtils.greaterThan(beer.tickValue, 0)) {
                            add(0, beer.id)
                        }
                    }
        }

        private fun requestSuccessful(responseBody: ResponseBody): Single<Boolean> {
            return Single.fromCallable {
                val value = responseBody.string().toLowerCase(Locale.ROOT)

                if (!SUCCESS_RESPONSES.any { value.contains(it) }) {
                    throw Exception("Unexpected response: " + value)
                }

                true
            }
        }

        fun getUniqueUri(id: Int, rating: Int): String {
            return Beer::class.java.toString() + "/" + id + "/rate/" + rating
        }
    }
}
