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
import io.reark.reark.utils.Preconditions.get
import quickbeer.android.data.columns.BrewerColumns
import quickbeer.android.data.pojos.Brewer
import quickbeer.android.data.providers.RateBeerProvider

class BrewerStoreCore(contentResolver: ContentResolver, gson: Gson) : StoreCoreBase<Int, Brewer>(contentResolver, gson) {

    override fun getUriForId(id: Int): Uri {
        return RateBeerProvider.Brewers.withId(get(id))
    }

    override fun getIdForUri(uri: Uri): Int {
        return RateBeerProvider.Brewers.fromUri(get(uri))
    }

    override fun getContentUri(): Uri {
        return RateBeerProvider.Brewers.BREWERS
    }

    override fun getProjection(): Array<String> {
        return arrayOf(BrewerColumns.ID, BrewerColumns.JSON, BrewerColumns.NAME)
    }

    override fun read(cursor: Cursor): Brewer {
        val json = cursor.getString(cursor.getColumnIndex(BrewerColumns.JSON))
        return gson.fromJson(json, Brewer::class.java)
    }

    override fun getContentValuesForItem(item: Brewer): ContentValues {
        return ContentValues().apply {
            put(BrewerColumns.ID, item.id)
            put(BrewerColumns.JSON, gson.toJson(item))
            put(BrewerColumns.NAME, item.name)
        }
    }

    override fun mergeValues(v1: Brewer, v2: Brewer): Brewer {
        return Brewer.merge(v1, v2)
    }
}
