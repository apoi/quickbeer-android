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

import quickbeer.android.data.columns.BrewerColumns;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.providers.RateBeerProvider;

import static io.reark.reark.utils.Preconditions.get;

public class BrewerStoreCore extends StoreCoreBase<Integer, Brewer> {

    public BrewerStoreCore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    @Override
    protected Uri getUriForId(@NonNull final Integer id) {
        return RateBeerProvider.Brewers.withId(get(id));
    }

    @NonNull
    @Override
    protected Integer getIdForUri(@NonNull final Uri uri) {
        return RateBeerProvider.Brewers.fromUri(get(uri));
    }

    @NonNull
    @Override
    protected Uri getContentUri() {
        return RateBeerProvider.Brewers.BREWERS;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] {
                BrewerColumns.ID,
                BrewerColumns.JSON,
                BrewerColumns.NAME
        };
    }

    @NonNull
    @Override
    protected Brewer read(@NonNull final Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BrewerColumns.JSON));
        return Brewer.builder(getGson().fromJson(json, Brewer.class))
                .build();
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull final Brewer item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BrewerColumns.ID, item.id());
        contentValues.put(BrewerColumns.JSON, getGson().toJson(item));
        contentValues.put(BrewerColumns.NAME, item.name());

        return contentValues;
    }

    @NonNull
    @Override
    protected Brewer mergeValues(@NonNull final Brewer v1, @NonNull final Brewer v2) {
        return Brewer.merge(v1, v2);
    }
}
