/**
 * This file is part of QuickBrewer.
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
package quickbeer.android.next.data.store;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.schematicprovider.BrewerListColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.pojo.ItemList;
import quickbeer.android.next.utils.DateUtils;

public class BrewerListStore extends StoreBase<ItemList<String>, String> {
    private static final String TAG = BrewerListStore.class.getSimpleName();

    public BrewerListStore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver, gson);
    }

    // Brewer search store needs separate query identifiers for normal searches and fixed searches
    // (top50, top in country, top in style). Fixed searches come attached with a service uri
    // identifier to make sure they stand apart from the normal searches.
    public String getQueryId(@NonNull Uri serviceUri, @NonNull String query) {
        Preconditions.checkNotNull(serviceUri, "Service uri cannot be null.");
        Preconditions.checkNotNull(query, "Query cannot be null.");

        if (serviceUri == RateBeerService.SEARCH) {
            return query;
        } else if (!query.isEmpty()) {
            return String.format("%s_%s", serviceUri, query);
        } else {
            return serviceUri.toString();
        }
    }

    public String getQueryId(@NonNull Uri serviceUri) {
        return getQueryId(serviceUri, "");
    }

    @NonNull
    @Override
    protected String getIdFor(@NonNull ItemList<String> list) {
        Preconditions.checkNotNull(list, "Search list cannot be null.");

        return list.getKey();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
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
    protected ContentValues getContentValuesForItem(ItemList<String> item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BrewerListColumns.KEY, item.getKey());
        contentValues.put(BrewerListColumns.JSON, getGson().toJson(item));
        contentValues.put(BrewerListColumns.UPDATED, DateUtils.toDbValue(item.getUpdateDate()));
        return contentValues;
    }

    @NonNull
    @Override
    protected ItemList<String> read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BrewerListColumns.JSON));
        final Date updated = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BrewerListColumns.UPDATED)));

        Type listType = new TypeToken<ItemList<String>>(){}.getType();
        ItemList<String> brewerList = getGson().fromJson(json, listType);
        brewerList.setUpdateDate(updated);

        return brewerList;
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull String id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.BrewerLists.withKey(id);
    }
}
