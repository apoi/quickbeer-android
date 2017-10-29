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

import org.threeten.bp.ZonedDateTime

import java.util.ArrayList

import quickbeer.android.data.columns.BrewerMetadataColumns
import quickbeer.android.data.pojos.BrewerMetadata
import quickbeer.android.data.providers.RateBeerProvider
import quickbeer.android.utils.DateUtils
import rx.Observable
import rx.schedulers.Schedulers

import io.reark.reark.utils.Preconditions.get

class BrewerMetadataStoreCore(contentResolver: ContentResolver, gson: Gson) : StoreCoreBase<Int, BrewerMetadata>(contentResolver, gson) {

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
        return RateBeerProvider.BrewerMetadata.withId(get(id))
    }

    override fun getIdForUri(uri: Uri): Int {
        return RateBeerProvider.BrewerMetadata.fromUri(get(uri))
    }

    override fun getContentUri(): Uri {
        return RateBeerProvider.BrewerMetadata.BREWER_METADATA
    }

    override fun getProjection(): Array<String> {
        return arrayOf(BrewerMetadataColumns.ID, BrewerMetadataColumns.UPDATED, BrewerMetadataColumns.ACCESSED)
    }

    override fun read(cursor: Cursor): BrewerMetadata {
        val brewerId = cursor.getInt(cursor.getColumnIndex(BrewerMetadataColumns.ID))
        val updated = DateUtils.fromEpochSecond(cursor.getInt(cursor.getColumnIndex(BrewerMetadataColumns.UPDATED)))
        val accessed = DateUtils.fromEpochSecond(cursor.getInt(cursor.getColumnIndex(BrewerMetadataColumns.ACCESSED)))

        return BrewerMetadata(brewerId, updated, accessed)
    }

    override fun getContentValuesForItem(item: BrewerMetadata): ContentValues {
        return ContentValues().apply {
            put(BrewerMetadataColumns.ID, item.brewerId)
            put(BrewerMetadataColumns.UPDATED, DateUtils.toEpochSecond(item.updated))
            put(BrewerMetadataColumns.ACCESSED, DateUtils.toEpochSecond(item.accessed))
        }
    }

    override fun mergeValues(v1: BrewerMetadata, v2: BrewerMetadata): BrewerMetadata {
        return BrewerMetadata.merge(v1, v2)
    }
}
