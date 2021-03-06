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
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import quickbeer.android.data.columns.BrewerMetadataColumns
import quickbeer.android.data.pojos.BrewerMetadata
import quickbeer.android.data.providers.RateBeerProvider
import quickbeer.android.utils.kotlin.ZonedDateTime
import quickbeer.android.utils.kotlin.orEpoch
import java.util.ArrayList

class BrewerMetadataStoreCore(contentResolver: ContentResolver, gson: Gson) :
    StoreCoreBase<Int, BrewerMetadata>(contentResolver, gson) {

    fun getAccessedIdsOnce(idColumn: String, accessColumn: String): Observable<List<Int>> {
        return Observable.fromCallable<List<Int>> {
            val projection = arrayOf(idColumn)
            val selection = String.format("%s > 0", accessColumn) // Has access date
            val orderBy = String.format("%s DESC", accessColumn) // Sort by date

            val idList = ArrayList<Int>(10)
            val cursor = contentResolver.query(contentUri, projection, selection, null, orderBy)

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        idList.add(cursor.getInt(cursor.getColumnIndex(idColumn)))
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }

            idList
        }
            .subscribeOn(Schedulers.io())
    }

    override fun getUriForId(id: Int): Uri {
        return RateBeerProvider.BrewerMetadata.withId(id)
    }

    override fun getIdForUri(uri: Uri): Int {
        return RateBeerProvider.BrewerMetadata.fromUri(uri)
    }

    override fun getContentUri(): Uri {
        return RateBeerProvider.BrewerMetadata.BREWER_METADATA
    }

    override fun getProjection(): Array<String> {
        return arrayOf(BrewerMetadataColumns.ID, BrewerMetadataColumns.UPDATED, BrewerMetadataColumns.ACCESSED)
    }

    override fun read(cursor: Cursor): BrewerMetadata {
        val brewerId = cursor.getInt(cursor.getColumnIndex(BrewerMetadataColumns.ID))
        val updated = ZonedDateTime(cursor.getInt(cursor.getColumnIndex(BrewerMetadataColumns.UPDATED)))
        val accessed = ZonedDateTime(cursor.getInt(cursor.getColumnIndex(BrewerMetadataColumns.ACCESSED)))

        return BrewerMetadata(brewerId, updated, accessed)
    }

    override fun getContentValuesForItem(item: BrewerMetadata): ContentValues {
        return ContentValues().apply {
            put(BrewerMetadataColumns.ID, item.brewerId)
            put(BrewerMetadataColumns.UPDATED, item.updated.orEpoch().toEpochSecond())
            put(BrewerMetadataColumns.ACCESSED, item.accessed.orEpoch().toEpochSecond())
        }
    }

    override fun mergeValues(v1: BrewerMetadata, v2: BrewerMetadata): BrewerMetadata {
        return BrewerMetadata.merge(v1, v2)
    }
}
