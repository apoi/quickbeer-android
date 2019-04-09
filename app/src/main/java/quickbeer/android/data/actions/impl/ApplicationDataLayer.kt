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
package quickbeer.android.data.actions.impl

import android.content.Context
import android.content.Intent
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reark.reark.data.DataStreamNotification
import io.reark.reark.data.utils.DataLayerUtils
import io.reark.reark.pojo.NetworkRequestStatus
import quickbeer.android.data.access.ServiceDataLayer
import quickbeer.android.network.NetworkService
import java.util.UUID

open class ApplicationDataLayer(protected val context: Context) {

    protected fun createServiceRequest(
        serviceUri: String,
        stringParams: Map<String, String> = emptyMap(),
        intParams: Map<String, Int> = emptyMap()
    ): Int {
        val listenerId = createListenerId()
        val intent = Intent(context, NetworkService::class.java).apply {
            putExtra(ServiceDataLayer.SERVICE_URI, serviceUri)
            putExtra(ServiceDataLayer.LISTENER_ID, listenerId)

            stringParams.forEach { putExtra(it.key, it.value) }
            intParams.forEach { putExtra(it.key, it.value) }
        }

        context.startService(intent)
        return listenerId
    }

    protected fun <T> createNotificationStream(
        statusStream: Observable<NetworkRequestStatus>,
        valueStream: Observable<T>
    ): Observable<DataStreamNotification<T>> {
        return DataLayerUtils.createDataStreamNotificationObservable(statusStream, valueStream)
            .subscribeOn(Schedulers.io())
    }

    private fun createListenerId(): Int {
        return UUID.randomUUID().hashCode()
    }
}
