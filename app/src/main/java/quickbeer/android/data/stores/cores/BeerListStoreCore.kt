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
import quickbeer.android.data.columns.BeerListColumns
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.providers.RateBeerProvider
import quickbeer.android.utils.kotlin.ZonedDateTime
import quickbeer.android.utils.kotlin.orEpoch

class BeerListStoreCore(contentResolver: ContentResolver, gson: Gson) :
    StoreCoreBase<String, ItemList<String>>(contentResolver, gson) {

    override fun getUriForId(id: String): Uri {
        return RateBeerProvider.BeerLists.withKey(get(id))
    }

    override fun getIdForUri(uri: Uri): String {
        return RateBeerProvider.BeerLists.fromUri(get(uri))
    }

    override fun getContentUri(): Uri {
        return RateBeerProvider.BeerLists.BEER_LISTS
    }

    override fun getProjection(): Array<String> {
        return arrayOf(BeerListColumns.KEY, BeerListColumns.JSON, BeerListColumns.UPDATED)
    }

    override fun read(cursor: Cursor): ItemList<String> {
        val json = cursor.getString(cursor.getColumnIndex(BeerListColumns.JSON))
        val updated = ZonedDateTime(cursor.getInt(cursor.getColumnIndex(BeerListColumns.UPDATED)))

        val listType = object : TypeToken<ItemList<String>>() {}.type
        val beerList = gson.fromJson<ItemList<String>>(json, listType)
        beerList.updateDate = updated

        return beerList
    }

    override fun getContentValuesForItem(item: ItemList<String>): ContentValues {
        return ContentValues().apply {
            put(BeerListColumns.KEY, item.key)
            put(BeerListColumns.JSON, gson.toJson(item))
            put(BeerListColumns.UPDATED, item.updateDate.orEpoch().toEpochSecond())
        }
    }
}
