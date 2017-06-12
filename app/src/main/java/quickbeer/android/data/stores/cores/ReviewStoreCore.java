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

import quickbeer.android.data.columns.ReviewColumns;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.data.providers.RateBeerProvider;

import static io.reark.reark.utils.Preconditions.get;

public class ReviewStoreCore extends StoreCoreBase<Integer, Review> {

    public ReviewStoreCore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    @Override
    protected Uri getUriForId(@NonNull Integer id) {
        return RateBeerProvider.Reviews.withId(get(id));
    }

    @NonNull
    @Override
    protected Integer getIdForUri(@NonNull Uri uri) {
        return RateBeerProvider.Reviews.fromUri(get(uri));
    }

    @NonNull
    @Override
    protected Uri getContentUri() {
        return RateBeerProvider.Reviews.REVIEWS;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] {
                ReviewColumns.ID,
                ReviewColumns.JSON,
                ReviewColumns.DRAFT,
                ReviewColumns.MODIFIED
        };
    }

    @NonNull
    @Override
    protected Review read(@NonNull Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(ReviewColumns.JSON));
        final boolean isDraft = cursor.getInt(cursor.getColumnIndex(ReviewColumns.DRAFT)) > 0;
        final boolean isModified = cursor.getInt(cursor.getColumnIndex(ReviewColumns.MODIFIED)) > 0;

        return getGson().fromJson(json, Review.class);
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull Review item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReviewColumns.ID, item.id());
        contentValues.put(ReviewColumns.JSON, getGson().toJson(item));

        return contentValues;
    }

    @NonNull
    @Override
    protected Review mergeValues(@NonNull Review v1, @NonNull Review v2) {
        return Review.merge(v1, v2);
    }
}
