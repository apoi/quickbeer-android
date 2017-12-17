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
import android.content.Intent
import io.reactivex.Observable
import io.reark.reark.data.DataStreamNotification
import io.reark.reark.data.utils.DataLayerUtils
import polanski.option.Option
import quickbeer.android.Constants
import quickbeer.android.data.actions.UserActions
import quickbeer.android.data.pojos.User
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.data.stores.UserStore
import quickbeer.android.network.NetworkService
import quickbeer.android.network.RateBeerService
import quickbeer.android.network.fetchers.LoginFetcher
import quickbeer.android.utils.kotlin.filterToValue
import timber.log.Timber
import javax.inject.Inject

class UserActionsImpl @Inject
constructor(context: Context,
            private val requestStatusStore: NetworkRequestStatusStore,
            private val userStore: UserStore)
    : ApplicationDataLayer(context), UserActions {

    override fun login(username: String, password: String): Observable<DataStreamNotification<User>> {
        Timber.v("login(%s)", username)

        val listenerId = createListenerId()
        val intent = Intent(context, NetworkService::class.java).apply {
            putExtra("serviceUriString", RateBeerService.LOGIN.toString())
            putExtra("listenerId", listenerId)
            putExtra("username", username)
            putExtra("password", password)
        }

        context.startService(intent)
        return getLoginStatus(listenerId)
    }

    override fun getLoginStatus(): Observable<DataStreamNotification<User>> {
        // Note -- returns old and new statuses alike
        return getLoginStatus(null)
    }

    private fun getLoginStatus(listenerId: Int?): Observable<DataStreamNotification<User>> {
        Timber.v("getLoginStatus()")

        val uri = LoginFetcher.getUniqueUri()

        val requestStatusObservable = requestStatusStore
                .getOnceAndStream(NetworkRequestStatusStore.requestIdForUri(uri))
                .filterToValue()
                .filter { it.forListener(listenerId) }

        val userObservable = userStore
                .getOnceAndStream(Constants.DEFAULT_USER_ID)
                .filterToValue()

        return DataLayerUtils.createDataStreamNotificationObservable(
                requestStatusObservable, userObservable)
    }

    override fun getUser(): Observable<Option<User>> {
        return userStore.getOnceAndStream(Constants.DEFAULT_USER_ID)
    }
}
