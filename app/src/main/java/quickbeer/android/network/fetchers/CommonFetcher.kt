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

package quickbeer.android.network.fetchers

import android.content.Intent
import io.reactivex.SingleTransformer
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reark.reark.network.fetchers.FetcherBase
import io.reark.reark.pojo.NetworkRequestStatus
import timber.log.Timber

abstract class CommonFetcher(
    networkRequestStatus: Consumer<NetworkRequestStatus>,
    val name: String
) : FetcherBase<String>(networkRequestStatus) {

    abstract fun requiredParams(): List<String>

    fun validateParams(intent: Intent): Boolean {
        requiredParams().forEach {
            if (!intent.hasExtra(it)) {
                Timber.e("Missing required fetch parameters $it!")
                return false
            }
        }

        return true
    }

    fun addFetcherTracking(id: Int, uri: String): SingleTransformer<Boolean, Boolean> {
        return SingleTransformer { source ->
            source
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { startRequest(id, uri) }
                .doOnSuccess { completeRequest(id, uri, it) }
                .doOnError(doOnError(id, uri))

        }
    }

    override fun getServiceUri() = name

    interface FetcherCompanion {
        val NAME: String

        fun getUniqueUri(id: Any = ""): String {
            return if (id.toString().isBlank()) NAME
            else "{$NAME}_$id"
        }

        fun getUniqueUri(id1: Any, id2: Any): String {
            return getUniqueUri("$id1:$id2")
        }
    }
}
