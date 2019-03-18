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
import io.reark.reark.utils.Preconditions.get
import quickbeer.android.data.columns.BrewerListColumns
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.providers.RateBeerProvider
import quickbeer.android.utils.kotlin.ZonedDateTime
import quickbeer.android.utils.kotlin.orEpoch

class BrewerListStoreCore(contentResolver: ContentResolver, gson: Gson) :
    StoreCoreBase<String, ItemList<String>>(contentResolver, gson) {

    override fun getUriForId(id: String): Uri {
        return RateBeerProvider.BrewerLists.withKey(get(id))
    }

    override fun getIdForUri(uri: Uri): String {
        return RateBeerProvider.BrewerLists.fromUri(get(uri))
    }

    override fun getContentUri(): Uri {
        return RateBeerProvider.BrewerLists.BREWER_LISTS
    }

    override fun getProjection(): Array<String> {
        return arrayOf(BrewerListColumns.KEY, BrewerListColumns.JSON, BrewerListColumns.UPDATED)
    }

    override fun read(cursor: Cursor): ItemList<String> {
        val json = cursor.getString(cursor.getColumnIndex(BrewerListColumns.JSON))
        val updated = ZonedDateTime(cursor.getInt(cursor.getColumnIndex(BrewerListColumns.UPDATED)))

        val listType = object : TypeToken<ItemList<String>>() {}.type
        val brewerList = gson.fromJson<ItemList<String>>(json, listType)
        brewerList.updateDate = updated

        return brewerList
    }

    override fun getContentValuesForItem(item: ItemList<String>): ContentValues {
        return ContentValues().apply {
            put(BrewerListColumns.KEY, item.key)
            put(BrewerListColumns.JSON, gson.toJson(item))
            put(BrewerListColumns.UPDATED, item.updateDate.orEpoch().toEpochSecond())
        }
    }
}
