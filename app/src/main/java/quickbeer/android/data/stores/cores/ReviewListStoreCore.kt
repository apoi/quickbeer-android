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
import com.google.gson.reflect.TypeToken
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.columns.ReviewListColumns
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.providers.RateBeerProvider
import quickbeer.android.utils.kotlin.ZonedDateTime
import quickbeer.android.utils.kotlin.orEpoch
import java.util.LinkedHashSet

class ReviewListStoreCore(contentResolver: ContentResolver, gson: Gson) :
    StoreCoreBase<Int, ItemList<Int>>(contentResolver, gson) {

    override fun getUriForId(id: Int): Uri {
        return RateBeerProvider.ReviewLists.withBeerId(id)
    }

    override fun getIdForUri(uri: Uri): Int {
        return RateBeerProvider.ReviewLists.fromUri(uri)
    }

    override fun getContentUri(): Uri {
        return RateBeerProvider.ReviewLists.REVIEW_LISTS
    }

    override fun getProjection(): Array<String> {
        return arrayOf(ReviewListColumns.BEER_ID, ReviewListColumns.JSON, ReviewListColumns.UPDATED)
    }

    override fun read(cursor: Cursor): ItemList<Int> {
        val json = cursor.getString(cursor.getColumnIndex(ReviewListColumns.JSON))
        val updated = ZonedDateTime(cursor.getInt(cursor.getColumnIndex(ReviewListColumns.UPDATED)))

        val listType = object : TypeToken<ItemList<Int>>() {}.type
        val reviewList = gson.fromJson<ItemList<Int>>(json, listType)
        reviewList.updateDate = updated

        return reviewList
    }

    override fun getContentValuesForItem(item: ItemList<Int>): ContentValues {
        return ContentValues().apply {
            put(ReviewListColumns.BEER_ID, item.key)
            put(ReviewListColumns.JSON, gson.toJson(item))
            put(ReviewListColumns.UPDATED, item.updateDate.orEpoch().toEpochSecond())
        }
    }

    override fun mergeValues(oldList: ItemList<Int>, newList: ItemList<Int>): ItemList<Int> {
        val items = LinkedHashSet<Int>(oldList.items.size + newList.items.size).apply {
            addAll(oldList.items)
            addAll(newList.items)
        }

        return ItemList.create(oldList.key, items.toList(), ZonedDateTime.now())
    }
}
