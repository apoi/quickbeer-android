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

import java.util.Date;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.schematicprovider.BeerSearchColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.pojo.BeerSearch;
import quickbeer.android.next.utils.DateUtils;

public class BeerSearchStore extends StoreBase<BeerSearch, String> {
    private static final String TAG = BeerSearchStore.class.getSimpleName();

    public BeerSearchStore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver, gson);
    }

    // Beer search store needs separate query identifiers for normal searches and fixed searches
    // (top50, top in country, top in style). Fixed searches come attached with a service uri
    // identifier to make sure they stand apart from the normal searches.
    public String getQueryId(@NonNull Uri serviceUri, @NonNull String query) {
        Preconditions.checkNotNull(serviceUri, "Service Uri cannot be null.");
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
    protected String getIdFor(@NonNull BeerSearch item) {
        Preconditions.checkNotNull(item, "Beer Search cannot be null.");

        return item.getSearch();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return RateBeerProvider.BeerSearches.BEER_SEARCHES;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] {
                BeerSearchColumns.SEARCH,
                BeerSearchColumns.JSON,
                BeerSearchColumns.UPDATED
        };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(BeerSearch item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BeerSearchColumns.SEARCH, item.getSearch());
        contentValues.put(BeerSearchColumns.JSON, getGson().toJson(item));
        contentValues.put(BeerSearchColumns.UPDATED, DateUtils.toDbValue(item.getUpdateDate()));
        return contentValues;
    }

    @NonNull
    @Override
    protected BeerSearch read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BeerSearchColumns.JSON));
        final Date updated = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BeerSearchColumns.UPDATED)));

        BeerSearch beerSearch = getGson().fromJson(json, BeerSearch.class);
        beerSearch.setUpdateDate(updated);

        return beerSearch;
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull String id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.BeerSearches.withSearch(id);
    }
}
