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
import quickbeer.android.next.data.schematicprovider.BeerListColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.pojo.ItemList;
import quickbeer.android.next.utils.DateUtils;

/**
 * Class storing beer lists related to a string key, such as a search.
 */
public class BeerListStore extends StoreBase<ItemList<String>, String> {
    private static final String TAG = BeerListStore.class.getSimpleName();

    public BeerListStore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver, gson);
    }

    // Beer list store needs separate query identifiers for normal searches and fixed searches
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
        Preconditions.checkNotNull(list, "Item list cannot be null.");

        return list.getKey();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return RateBeerProvider.BeerLists.BEER_LISTS;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] {
                BeerListColumns.KEY,
                BeerListColumns.JSON,
                BeerListColumns.UPDATED
        };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(ItemList<String> item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BeerListColumns.KEY, item.getKey());
        contentValues.put(BeerListColumns.JSON, getGson().toJson(item));
        contentValues.put(BeerListColumns.UPDATED, DateUtils.toDbValue(item.getUpdateDate()));
        return contentValues;
    }

    @NonNull
    @Override
    protected ItemList<String> read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BeerListColumns.JSON));
        final Date updated = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BeerListColumns.UPDATED)));

        Type listType = new TypeToken<ItemList<String>>(){}.getType();
        ItemList<String> beerList = getGson().fromJson(json, listType);
        beerList.setUpdateDate(updated);

        return beerList;
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull String id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.BeerLists.withKey(id);
    }
}
