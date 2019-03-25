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
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reark.reark.pojo.NetworkRequestStatus
import quickbeer.android.Constants
import quickbeer.android.data.pojos.User
import quickbeer.android.data.stores.UserStore
import quickbeer.android.network.NetworkApi
import quickbeer.android.network.fetchers.CheckingFetcher
import quickbeer.android.network.utils.LoginUtils
import timber.log.Timber

class LoginFetcher(
    private val networkApi: NetworkApi,
    private val cookieJar: ClearableCookieJar,
    networkRequestStatus: Consumer<NetworkRequestStatus>,
    private val userStore: UserStore
) : CheckingFetcher(networkRequestStatus, NAME) {

    override fun required() = listOf(USERNAME, PASSWORD)

    override fun fetch(intent: Intent, listenerId: Int) {
        if (!validateParams(intent)) return

        val uri = getUniqueUri()
        val requestId = uri.hashCode()

        addListener(requestId, listenerId)

        if (isOngoingRequest(requestId)) {
            Timber.d("Found an ongoing request for login")
            return
        }

        val username = intent.getStringExtra(USERNAME)
        val password = intent.getStringExtra(PASSWORD)

        if (username.isBlank() || password.isBlank()) {
            Timber.d("Missing username or password")
            return
        }

        cookieJar.clear()

        Timber.d("Login with user $username")

        networkApi.login(username, password)
            .subscribeOn(Schedulers.io())
            .map { LoginUtils.getUserId(cookieJar) }
            .doOnSuccess { id -> id.ifNone { Timber.e("No user id found in login response!") } }
            .map { userId -> User(userId.orDefault { -1 }, username, password) }
            .flatMap { userStore.put(it) }
            .doOnSubscribe { startRequest(requestId, uri) }
            .doOnSuccess { updated -> completeRequest(requestId, uri, updated!!) }
            .doOnError(doOnError(requestId, uri))
            .subscribe({}, { error -> Timber.e(error, "Error fetching user $username") })
            .also { addRequest(requestId, it) }
    }

    companion object {
        const val NAME = "__login"
        const val USERNAME = "username"
        const val PASSWORD = "password"

        fun getUniqueUri(): String {
            return User::class.java.toString() + "/" + Constants.DEFAULT_USER_ID
        }
    }
}
