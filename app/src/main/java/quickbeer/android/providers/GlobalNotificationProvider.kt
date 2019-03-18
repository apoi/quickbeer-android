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

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reark.reark.data.DataStreamNotification
import io.reark.reark.data.DataStreamNotification.Type.COMPLETED_WITHOUT_VALUE
import io.reark.reark.data.DataStreamNotification.Type.COMPLETED_WITH_ERROR
import io.reark.reark.data.DataStreamNotification.Type.COMPLETED_WITH_VALUE
import io.reark.reark.data.DataStreamNotification.Type.ONGOING
import io.reark.reark.data.DataStreamNotification.Type.ON_NEXT
import timber.log.Timber
import java.util.HashMap
import java.util.concurrent.atomic.AtomicInteger

class GlobalNotificationProvider(private val toastProvider: ToastProvider) {

    private val counter: AtomicInteger = AtomicInteger(0)
    private val disposableList = HashMap<Int, Disposable>(10)

    fun addNetworkSuccessListener(
        observable: Observable<DataStreamNotification<Void>>,
        successToast: String,
        failureToast: String
    ) {
        val index = counter.incrementAndGet()
        val disposable = observable
            .takeUntil { it.isCompleted }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { disposableList.remove(index) }
            .subscribe({ handleType(it.type, successToast, failureToast) },
                { Timber.w(it, "Global listener error") })

        disposableList.put(index, disposable)
    }

    private fun handleType(type: DataStreamNotification.Type, successToast: String, failureToast: String) {
        when (type) {
            COMPLETED_WITH_VALUE, COMPLETED_WITHOUT_VALUE -> showToast(successToast)
            COMPLETED_WITH_ERROR -> showToast(failureToast)
            ONGOING, ON_NEXT -> {
            }
        }
    }

    private fun showToast(text: String) {
        toastProvider.showToast(text)
    }
}
