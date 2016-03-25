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
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.data.schematicprovider.ReviewListColumns;
import quickbeer.android.next.pojo.ReviewList;
import quickbeer.android.next.utils.DateUtils;

public class ReviewListStore extends StoreBase<ReviewList, Integer> {
    private static final String TAG = ReviewListStore.class.getSimpleName();

    public ReviewListStore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull ReviewList item) {
        Preconditions.checkNotNull(item, "ReviewList cannot be null.");

        return item.getBeerId();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return RateBeerProvider.ReviewLists.REVIEW_LISTS;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] {
                ReviewListColumns.BEER_ID,
                ReviewListColumns.JSON,
                ReviewListColumns.UPDATED
        };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(ReviewList item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReviewListColumns.BEER_ID, item.getBeerId());
        contentValues.put(ReviewListColumns.JSON, getGson().toJson(item));
        contentValues.put(ReviewListColumns.UPDATED, DateUtils.toDbValue(item.getUpdateDate()));
        return contentValues;
    }

    @NonNull
    @Override
    protected ReviewList read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(ReviewListColumns.JSON));
        final Date updated = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(ReviewListColumns.UPDATED)));

        ReviewList reviewList = getGson().fromJson(json, ReviewList.class);
        reviewList.setUpdateDate(updated);

        return reviewList;
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.ReviewLists.withBeerId(id);
    }
}
