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
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;

import quickbeer.android.data.columns.BeerListColumns;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.providers.RateBeerProvider;
import quickbeer.android.utils.DateUtils;

import static io.reark.reark.utils.Preconditions.get;

public class BeerListStoreCore extends StoreCoreBase<String, ItemList<String>> {

    public BeerListStoreCore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    @Override
    protected Uri getUriForId(@NonNull final String id) {
        return RateBeerProvider.BeerLists.withKey(get(id));
    }

    @NonNull
    @Override
    protected String getIdForUri(@NonNull final Uri uri) {
        return RateBeerProvider.BeerLists.fromUri(get(uri));
    }

    @NonNull
    @Override
    protected Uri getContentUri() {
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

    @SuppressWarnings("EmptyClass")
    @NonNull
    @Override
    protected ItemList<String> read(@NonNull final Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BeerListColumns.JSON));
        final DateTime updated = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BeerListColumns.UPDATED)));

        final Type listType = new TypeToken<ItemList<String>>(){}.getType();
        ItemList<String> beerList = getGson().fromJson(json, listType);
        beerList.setUpdateDate(updated);

        return beerList;
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull final ItemList<String> item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BeerListColumns.KEY, item.getKey());
        contentValues.put(BeerListColumns.JSON, getGson().toJson(item));
        contentValues.put(BeerListColumns.UPDATED, DateUtils.toDbValue(item.getUpdateDate()));

        return contentValues;
    }
}
