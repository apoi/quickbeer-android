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
import quickbeer.android.data.columns.BeerMetadataColumns
import quickbeer.android.data.pojos.BeerMetadata
import quickbeer.android.data.providers.RateBeerProvider
import quickbeer.android.utils.ValueUtils
import quickbeer.android.utils.kotlin.ZonedDateTime
import quickbeer.android.utils.kotlin.orEpoch
import java.util.ArrayList

class BeerMetadataStoreCore(contentResolver: ContentResolver, gson: Gson) :
    StoreCoreBase<Int, BeerMetadata>(contentResolver, gson) {

    fun getAccessedIdsOnce(idColumn: String, accessColumn: String): Observable<List<Int>> {
        return Observable
            .fromCallable<List<Int>> {
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
        return RateBeerProvider.BeerMetadata.withId(id)
    }

    override fun getIdForUri(uri: Uri): Int {
        return RateBeerProvider.BeerMetadata.fromUri(uri)
    }

    override fun getContentUri(): Uri {
        return RateBeerProvider.BeerMetadata.BEER_METADATA
    }

    override fun getProjection(): Array<String> {
        return arrayOf(
            BeerMetadataColumns.ID,
            BeerMetadataColumns.UPDATED,
            BeerMetadataColumns.ACCESSED,
            BeerMetadataColumns.REVIEW_ID,
            BeerMetadataColumns.MODIFIED
        )
    }

    override fun read(cursor: Cursor): BeerMetadata {
        val beerId = cursor.getInt(cursor.getColumnIndex(BeerMetadataColumns.ID))
        val updated = ZonedDateTime(cursor.getInt(cursor.getColumnIndex(BeerMetadataColumns.UPDATED)))
        val accessed = ZonedDateTime(cursor.getInt(cursor.getColumnIndex(BeerMetadataColumns.ACCESSED)))
        val reviewId = cursor.getInt(cursor.getColumnIndex(BeerMetadataColumns.REVIEW_ID))
        val isModified = cursor.getInt(cursor.getColumnIndex(BeerMetadataColumns.MODIFIED)) > 0

        return BeerMetadata(beerId, updated, accessed, reviewId, isModified)
    }

    override fun getContentValuesForItem(item: BeerMetadata): ContentValues {
        return ContentValues().apply {
            put(BeerMetadataColumns.ID, item.beerId)
            put(BeerMetadataColumns.UPDATED, item.updated.orEpoch().toEpochSecond())
            put(BeerMetadataColumns.ACCESSED, item.accessed.orEpoch().toEpochSecond())
            put(BeerMetadataColumns.REVIEW_ID, item.reviewId)
            put(BeerMetadataColumns.MODIFIED, ValueUtils.asInt(item.isModified))
        }
    }

    override fun mergeValues(v1: BeerMetadata, v2: BeerMetadata): BeerMetadata {
        return BeerMetadata.merge(v1, v2)
    }
}
