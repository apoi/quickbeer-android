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

import org.threeten.bp.ZonedDateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import quickbeer.android.data.columns.ReviewListColumns;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.providers.RateBeerProvider;
import quickbeer.android.utils.DateUtils;

import static io.reark.reark.utils.Preconditions.get;

public class ReviewListStoreCore extends StoreCoreBase<Integer, ItemList<Integer>> {

    public ReviewListStoreCore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    @Override
    protected Uri getUriForId(@NonNull Integer id) {
        return RateBeerProvider.ReviewLists.withBeerId(get(id));
    }

    @NonNull
    @Override
    protected Integer getIdForUri(@NonNull Uri uri) {
        return RateBeerProvider.ReviewLists.fromUri(get(uri));
    }

    @NonNull
    @Override
    protected Uri getContentUri() {
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

    @SuppressWarnings("EmptyClass")
    @NonNull
    @Override
    protected ItemList<Integer> read(@NonNull Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(ReviewListColumns.JSON));
        final ZonedDateTime updated = DateUtils.fromEpochSecond(cursor.getInt(cursor.getColumnIndex(ReviewListColumns.UPDATED)));

        Type listType = new TypeToken<ItemList<Integer>>(){}.getType();
        ItemList<Integer> reviewList = getGson().fromJson(json, listType);
        reviewList.setUpdateDate(updated);

        return reviewList;
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull ItemList<Integer> item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReviewListColumns.BEER_ID, item.getKey());
        contentValues.put(ReviewListColumns.JSON, getGson().toJson(item));
        contentValues.put(ReviewListColumns.UPDATED, DateUtils.toEpochSecond(item.getUpdateDate()));

        return contentValues;
    }

    @NonNull
    @Override
    protected ItemList<Integer> mergeValues(@NonNull ItemList<Integer> oldList, @NonNull ItemList<Integer> newList) {
        Set<Integer> items = new LinkedHashSet<>(oldList.getItems().size() + newList.getItems().size());
        items.addAll(oldList.getItems());
        items.addAll(newList.getItems());

        return ItemList.Companion.create(oldList.getKey(), new ArrayList<>(items), ZonedDateTime.now());
    }
}
