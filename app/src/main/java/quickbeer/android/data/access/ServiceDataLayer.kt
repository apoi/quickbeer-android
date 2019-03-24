/**
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
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.data.access

import android.content.Intent
import quickbeer.android.network.fetchers.FetcherManager
import quickbeer.android.utils.kotlin.ifNull
import timber.log.Timber

class ServiceDataLayer(private val fetcherManager: FetcherManager) {

    fun processIntent(intent: Intent) {
        if (!intent.hasExtra(SERVICE_URI)) {
            Timber.e("No service uri defined")
            return
        }

        if (!intent.hasExtra(LISTENER_ID)) {
            Timber.e("No listener id defined")
            return
        }

        val serviceUri = intent.getStringExtra(SERVICE_URI)
        val listenerId = intent.getIntExtra(LISTENER_ID, 0)

        fetcherManager.findFetcher(serviceUri)
            ?.fetch(intent, listenerId)
            .ifNull {
                Timber.e("Unknown Uri $serviceUri")
            }
    }

    companion object {
        const val SERVICE_URI = "serviceUriString"
        const val LISTENER_ID = "listenerId"
    }
}
