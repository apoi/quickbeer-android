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
import io.reactivex.functions.Consumer
import io.reark.reark.network.fetchers.FetcherBase
import io.reark.reark.pojo.NetworkRequestStatus
import timber.log.Timber

abstract class CheckingFetcher(
    networkRequestStatus: Consumer<NetworkRequestStatus>,
    val name: String
) : FetcherBase<String>(networkRequestStatus) {

    abstract fun required(): List<String>

    fun validateParams(intent: Intent): Boolean {
        required().forEach {
            if (!intent.hasExtra(it)) {
                Timber.e("Missing required fetch parameters $it!")
                return false
            }
        }

        return true
    }

    override fun getServiceUri() = name
}
