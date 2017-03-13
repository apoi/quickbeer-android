/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.data.stores.cores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import quickbeer.android.Constants;
import quickbeer.android.data.columns.JsonIdColumns;
import quickbeer.android.data.columns.UserColumns;
import quickbeer.android.data.pojos.User;
import quickbeer.android.data.providers.RateBeerProvider;

import static io.reark.reark.utils.Preconditions.get;

public class UserStoreCore extends StoreCoreBase<Integer, User> {

    public UserStoreCore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    @Override
    protected Uri getUriForId(@NonNull final Integer id) {
        return RateBeerProvider.Users.withId(get(id));
    }

    @NonNull
    @Override
    protected Integer getIdForUri(@NonNull final Uri uri) {
        return RateBeerProvider.Users.fromUri(uri);
    }

    @NonNull
    @Override
    protected Uri getContentUri() {
        return RateBeerProvider.Users.USERS;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] {
                UserColumns.ID,
                UserColumns.JSON
        };
    }

    @NonNull
    @Override
    protected User read(@NonNull final Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON));
        return getGson().fromJson(json, User.class);
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull final User item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(JsonIdColumns.ID, Constants.DEFAULT_USER_ID);
        contentValues.put(JsonIdColumns.JSON, getGson().toJson(item));

        return contentValues;
    }
}
