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
package quickbeer.android.data.stores.cores

import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.google.gson.Gson
import io.reark.reark.pojo.NetworkRequestStatus
import quickbeer.android.data.columns.JsonIdColumns
import quickbeer.android.data.columns.NetworkRequestStatusColumns
import quickbeer.android.data.providers.RateBeerProvider
import rx.Observable

class NetworkRequestStatusStoreCore(contentResolver: ContentResolver, gson: Gson)
    : StoreCoreBase<Int, NetworkRequestStatus>(contentResolver, gson) {

    override fun <R> groupOperations(source: Observable<R>): Observable<List<R>> {
        // NetworkRequestStatus updates should not be grouped to ensure fast processing.
        return source.map { listOf(it) }
    }

    public override fun getUriForId(id: Int): Uri {
        return RateBeerProvider.NetworkRequestStatuses.withId(id.toLong())
    }

    override fun getIdForUri(uri: Uri): Int {
        return RateBeerProvider.NetworkRequestStatuses.fromUri(uri).toInt()
    }

    public override fun getContentUri(): Uri {
        return RateBeerProvider.NetworkRequestStatuses.NETWORK_REQUEST_STATUSES
    }

    override fun getProjection(): Array<String> {
        return arrayOf(NetworkRequestStatusColumns.ID, NetworkRequestStatusColumns.JSON)
    }

    override fun read(cursor: Cursor): NetworkRequestStatus {
        val json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON))
        return gson.fromJson(json, NetworkRequestStatus::class.java)
    }

    override fun getContentValuesForItem(item: NetworkRequestStatus): ContentValues {
        return ContentValues().apply {
            put(JsonIdColumns.ID, item.uri.hashCode())
            put(JsonIdColumns.JSON, gson.toJson(item))
        }
    }
}
