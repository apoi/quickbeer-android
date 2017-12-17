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
import quickbeer.android.data.columns.BeerColumns
import quickbeer.android.data.pojos.Beer
import quickbeer.android.data.providers.RateBeerProvider
import quickbeer.android.utils.kotlin.ZonedDateTime
import quickbeer.android.utils.kotlin.orEpoch

class BeerStoreCore(contentResolver: ContentResolver, gson: Gson)
    : StoreCoreBase<Int, Beer>(contentResolver, gson) {

    override fun getUriForId(id: Int): Uri {
        return RateBeerProvider.Beers.withId(get(id))
    }

    override fun getIdForUri(uri: Uri): Int {
        return RateBeerProvider.Beers.fromUri(get(uri))
    }

    override fun getContentUri(): Uri {
        return RateBeerProvider.Beers.BEERS
    }

    override fun getProjection(): Array<String> {
        return arrayOf(BeerColumns.ID, BeerColumns.JSON, BeerColumns.NAME, BeerColumns.TICK_VALUE, BeerColumns.TICK_DATE)
    }

    override fun read(cursor: Cursor): Beer {
        val json = cursor.getString(cursor.getColumnIndex(BeerColumns.JSON))
        val value = cursor.getInt(cursor.getColumnIndex(BeerColumns.TICK_VALUE))
        val date = ZonedDateTime(cursor.getInt(cursor.getColumnIndex(BeerColumns.TICK_DATE)))

        return gson.fromJson(json, Beer::class.java)
                .copy(tickValue = value, tickDate = date)
    }

    override fun getContentValuesForItem(item: Beer): ContentValues {
        return ContentValues().apply {
            put(BeerColumns.ID, item.id)
            put(BeerColumns.JSON, gson.toJson(item))
            put(BeerColumns.NAME, item.name)
            put(BeerColumns.TICK_VALUE, item.tickValue)
            put(BeerColumns.TICK_DATE, item.tickDate.orEpoch().toEpochSecond())
        }
    }

    override fun mergeValues(v1: Beer, v2: Beer): Beer {
        return Beer.merge(v1, v2)
    }
}
