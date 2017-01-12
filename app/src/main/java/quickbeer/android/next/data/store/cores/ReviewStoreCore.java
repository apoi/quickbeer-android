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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.data.schematicprovider.ReviewColumns;
import quickbeer.android.next.pojo.Review;

public class ReviewStoreCore extends StoreCoreBase<Integer, Review> {
    private static final String TAG = ReviewStoreCore.class.getSimpleName();

    public ReviewStoreCore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    @Override
    protected Uri getUriForId(@NonNull final Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.Reviews.withId(id);
    }

    @NonNull
    @Override
    protected Integer getIdForUri(@NonNull final Uri uri) {
        Preconditions.checkNotNull(uri, "Uri cannot be null.");

        return RateBeerProvider.Reviews.fromUri(uri);
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
    protected Review read(@NonNull final Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(ReviewColumns.JSON));
        final boolean isDraft = cursor.getInt(cursor.getColumnIndex(ReviewColumns.DRAFT)) > 0;
        final boolean isModified = cursor.getInt(cursor.getColumnIndex(ReviewColumns.MODIFIED)) > 0;

        Review review = getGson().fromJson(json, Review.class);
        review.setIsDraft(isDraft);
        review.setIsModified(isModified);

        return review;
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull final Review item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReviewColumns.ID, item.getId());
        contentValues.put(ReviewColumns.JSON, getGson().toJson(item));
        contentValues.put(ReviewColumns.DRAFT, item.isDraft() ? 1 : 0);
        contentValues.put(ReviewColumns.MODIFIED, item.isModified() ? 1 : 0);

        return contentValues;
    }

    @NonNull
    @Override
    protected Review mergeValues(@NonNull final Review v1, @NonNull final Review v2) {
        Review newValue = new Review();
        newValue.overwrite(v1);
        newValue.overwrite(v2);

        return newValue;
    }
}
