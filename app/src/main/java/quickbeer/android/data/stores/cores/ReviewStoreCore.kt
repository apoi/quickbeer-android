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
import quickbeer.android.data.columns.ReviewColumns
import quickbeer.android.data.pojos.Review
import quickbeer.android.data.providers.RateBeerProvider

class ReviewStoreCore(contentResolver: ContentResolver, gson: Gson) :
    StoreCoreBase<Int, Review>(contentResolver, gson) {

    override fun getUriForId(id: Int): Uri {
        return RateBeerProvider.Reviews.withId(get(id))
    }

    override fun getIdForUri(uri: Uri): Int {
        return RateBeerProvider.Reviews.fromUri(get(uri))
    }

    override fun getContentUri(): Uri {
        return RateBeerProvider.Reviews.REVIEWS
    }

    override fun getProjection(): Array<String> {
        return arrayOf(ReviewColumns.ID, ReviewColumns.JSON, ReviewColumns.DRAFT, ReviewColumns.MODIFIED)
    }

    override fun read(cursor: Cursor): Review {
        val json = cursor.getString(cursor.getColumnIndex(ReviewColumns.JSON))
        val isDraft = cursor.getInt(cursor.getColumnIndex(ReviewColumns.DRAFT)) > 0
        val isModified = cursor.getInt(cursor.getColumnIndex(ReviewColumns.MODIFIED)) > 0

        return gson.fromJson(json, Review::class.java)
    }

    override fun getContentValuesForItem(item: Review): ContentValues {
        return ContentValues().apply {
            put(ReviewColumns.ID, item.id)
            put(ReviewColumns.JSON, gson.toJson(item))
        }
    }

    override fun mergeValues(v1: Review, v2: Review): Review {
        return Review.merge(v1, v2)
    }
}
