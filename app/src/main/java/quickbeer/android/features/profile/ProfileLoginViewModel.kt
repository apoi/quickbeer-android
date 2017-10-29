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
package quickbeer.android.features.profile

import android.support.v4.util.Pair
import io.reark.reark.data.DataStreamNotification
import polanski.option.Option
import quickbeer.android.R
import quickbeer.android.core.viewmodel.SimpleViewModel
import quickbeer.android.data.actions.ReviewActions
import quickbeer.android.data.actions.UserActions
import quickbeer.android.data.pojos.User
import quickbeer.android.providers.ResourceProvider
import quickbeer.android.rx.RxUtils
import quickbeer.android.rx.Unit
import quickbeer.android.utils.StringUtils
import rx.Observable
import rx.subjects.PublishSubject
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import javax.inject.Inject

class ProfileLoginViewModel @Inject
internal constructor(private val userActions: UserActions,
                     private val reviewActions: ReviewActions,
                     private val resourceProvider: ResourceProvider)
    : SimpleViewModel() {

    private val loginSubject = PublishSubject.create<Pair<String, String>>()

    private val autoLoginSubject = PublishSubject.create<Unit>()

    private val loginCompletedSubject = PublishSubject.create<Boolean>()

    private val errorSubject = PublishSubject.create<String>()

    override fun bind(subscription: CompositeSubscription) {
        subscription.add(loginSubject.asObservable()
                .switchMap { userActions.login(it.first, it.second) }
                .subscribe({ handleNotification(it) }, { Timber.e(it) }))

        subscription.add(autoLoginSubject.asObservable()
                .switchMap { getUser() }
                .compose { RxUtils.pickValue(it) }
                .subscribe({ login(it.username, it.password) }, { Timber.e(it) }))
    }

    fun login(username: String, password: String) {
        loginSubject.onNext(Pair.create(username, password))
    }

    fun autoLogin() {
        autoLoginSubject.onNext(Unit.DEFAULT)
    }

    fun fetchTicks(userId: Int) {
        reviewActions.fetchTicks(userId.toString())
    }

    fun getIsLoginInProgress(): Observable<Boolean> {
        return userActions.getLoginStatus().map{ it.isOngoing() }
    }

    fun loginCompletedStream(): Observable<Boolean> {
        return loginCompletedSubject
    }

    fun errorStream(): Observable<String> {
        return errorSubject.asObservable()
                .map { this.toReadableError(it) }
    }

    fun getUser(): Observable<Option<User>> {
        return userActions.getUser()
    }

    private fun handleNotification(notification: DataStreamNotification<User>) {
        if (notification.isCompleted) {
            loginCompletedSubject.onNext(notification.isCompletedWithSuccess)
        }
        if (notification.isCompletedWithError) {
            errorSubject.onNext(notification.error)
        }
    }

    private fun toReadableError(errorMessage: String): String {
        // We use 403 for when result is a known login failure page. For other errors,
        // just show a generic error message.
        return resourceProvider.getString(if (StringUtils.value(errorMessage).contains("403"))
            R.string.login_failed
        else
            R.string.login_error)
    }
}
