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
package quickbeer.android.network.fetchers.actions

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.Function
import okhttp3.ResponseBody
import org.reactivestreams.Publisher
import quickbeer.android.Constants
import quickbeer.android.data.pojos.User
import quickbeer.android.data.stores.UserStore
import quickbeer.android.network.NetworkApi
import quickbeer.android.utils.kotlin.filterToValue
import retrofit2.HttpException
import timber.log.Timber

class LoginAndRetry(
    private val networkApi: NetworkApi,
    private val userStore: UserStore
) : Function<Flowable<Throwable>, Publisher<Any>> {

    override fun apply(error: Flowable<Throwable>): Publisher<Any> {
        return error.flatMap { throwable -> handleError(throwable) }
    }

    private fun handleError(throwable: Throwable): Flowable<ResponseBody> {
        // Attempt re-login with every HTTP error
        if (throwable is HttpException) {
            Timber.d("Error %s, retrying operation after login", throwable.code())
            return getUserOrError(throwable)
                .flatMapSingle { user -> networkApi.login(user.username, user.password) }
                .doOnError { Timber.w("Retry failed, propagating login error") }
        }

        Timber.d("Not a Retrofit error, will not retry")
        return Flowable.error(throwable)
    }

    private fun getUserOrError(throwable: Throwable): Flowable<User> {
        return userStore.getOnce(Constants.DEFAULT_USER_ID)
            .toObservable()
            .filterToValue()
            .switchIfEmpty(Observable.error(throwable))
            .doOnError { Timber.w("No login details available!") }
            .toFlowable(BackpressureStrategy.BUFFER)
    }
}