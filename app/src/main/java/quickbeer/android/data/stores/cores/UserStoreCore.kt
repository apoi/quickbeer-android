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
import quickbeer.android.Constants
import quickbeer.android.data.columns.JsonIdColumns
import quickbeer.android.data.columns.UserColumns
import quickbeer.android.data.pojos.User
import quickbeer.android.data.providers.RateBeerProvider

class UserStoreCore(contentResolver: ContentResolver, gson: Gson) :
    StoreCoreBase<Int, User>(contentResolver, gson) {

    override fun getUriForId(id: Int): Uri {
        return RateBeerProvider.Users.withId(id)
    }

    override fun getIdForUri(uri: Uri): Int {
        return RateBeerProvider.Users.fromUri(uri)
    }

    override fun getContentUri(): Uri {
        return RateBeerProvider.Users.USERS
    }

    override fun getProjection(): Array<String> {
        return arrayOf(UserColumns.ID, UserColumns.JSON)
    }

    override fun read(cursor: Cursor): User {
        val json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON))
        return gson.fromJson(json, User::class.java)
    }

    override fun getContentValuesForItem(item: User): ContentValues {
        return ContentValues().apply {
            put(JsonIdColumns.ID, Constants.DEFAULT_USER_ID)
            put(JsonIdColumns.JSON, gson.toJson(item))
        }
    }
}
