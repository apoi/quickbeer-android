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

package quickbeer.android.providers

import io.reark.reark.data.DataStreamNotification
import quickbeer.android.R
import quickbeer.android.data.DataLayer
import quickbeer.android.data.pojos.User
import quickbeer.android.rx.RxUtils
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Inject

class AutoLoginProvider @Inject
internal constructor(private val getUser: DataLayer.GetUser,
                     private val login: DataLayer.Login,
                     private val toastProvider: ToastProvider) {

    fun login() {
        getUser.call()
                .first()
                .compose { RxUtils.pickValue(it) }
                .flatMap { login.call(it.username(), it.password()) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ handleNotification(it) }, { Timber.e(it) })
    }

    private fun handleNotification(notification: DataStreamNotification<User>) {
        if (notification.isCompletedWithError) {
            toastProvider.showToast(R.string.auto_login_failed)
        }
    }
}
