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
package quickbeer.android.next.data.store.cores;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.lang.reflect.Type;
import java.util.Date;

import quickbeer.android.next.data.schematicprovider.BrewerListColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.pojo.ItemList;
import quickbeer.android.next.utils.DateUtils;

import static io.reark.reark.utils.Preconditions.get;

public class BrewerListStoreCore extends StoreCoreBase<String, ItemList<String>> {

    public BrewerListStoreCore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    @Override
    protected Uri getUriForId(@NonNull final String id) {
        return RateBeerProvider.BrewerLists.withKey(get(id));
    }

    @NonNull
    @Override
    protected String getIdForUri(@NonNull final Uri uri) {
        return RateBeerProvider.BrewerLists.fromUri(get(uri));
    }

    @NonNull
    @Override
    protected Uri getContentUri() {
        return RateBeerProvider.BrewerLists.BREWER_LISTS;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] {
                BrewerListColumns.KEY,
                BrewerListColumns.JSON,
                BrewerListColumns.UPDATED
        };
    }

    @NonNull
    @Override
    protected ItemList<String> read(@NonNull final Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BrewerListColumns.JSON));
        final Date updated = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BrewerListColumns.UPDATED)));

        final Type listType = new TypeToken<ItemList<String>>(){}.getType();
        ItemList<String> brewerList = getGson().fromJson(json, listType);
        brewerList.setUpdateDate(updated);

        return brewerList;
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull final ItemList<String> item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BrewerListColumns.KEY, item.getKey());
        contentValues.put(BrewerListColumns.JSON, getGson().toJson(item));
        contentValues.put(BrewerListColumns.UPDATED, DateUtils.toDbValue(item.getUpdateDate()));

        return contentValues;
    }
}
