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
import quickbeer.android.next.data.schematicprovider.BeerColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.utils.DateUtils;

public class BeerStore extends StoreBase<Beer, Integer> {
    private static final String TAG = BeerStore.class.getSimpleName();

    public BeerStore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull Beer item) {
        Preconditions.checkNotNull(item, "Beer cannot be null.");

        return item.getId();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return RateBeerProvider.Beers.BEERS;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] {
                BeerColumns.ID,
                BeerColumns.JSON,
                BeerColumns.NAME,
                BeerColumns.TICK,
                BeerColumns.REVIEW,
                BeerColumns.MODIFIED,
                BeerColumns.UPDATED,
                BeerColumns.ACCESSED
        };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(Beer item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BeerColumns.ID, item.getId());
        contentValues.put(BeerColumns.JSON, getGson().toJson(item));
        contentValues.put(BeerColumns.NAME, item.getName());
        contentValues.put(BeerColumns.TICK, item.getTick());
        contentValues.put(BeerColumns.REVIEW, item.getReviewId());
        contentValues.put(BeerColumns.MODIFIED, item.isModified() ? 1 : 0);
        contentValues.put(BeerColumns.UPDATED, DateUtils.toDbValue(item.getUpdateDate()));
        contentValues.put(BeerColumns.ACCESSED, DateUtils.toDbValue(item.getAccessDate()));

        return contentValues;
    }

    @NonNull
    @Override
    protected Beer read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BeerColumns.JSON));
        final int tick = cursor.getInt(cursor.getColumnIndex(BeerColumns.TICK));
        final int reviewId = cursor.getInt(cursor.getColumnIndex(BeerColumns.REVIEW));
        final boolean isModified = cursor.getInt(cursor.getColumnIndex(BeerColumns.MODIFIED)) > 0;
        final Date updated = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BeerColumns.UPDATED)));
        final Date accessed = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BeerColumns.ACCESSED)));

        Beer beer = getGson().fromJson(json, Beer.class);
        beer.setTick(tick);
        beer.setReviewId(reviewId);
        beer.setIsModified(isModified);
        beer.setUpdateDate(updated);
        beer.setAccessDate(accessed);

        return beer;
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.Beers.withId(id);
    }

    @NonNull
    @Override
    protected Beer mergeValues(@NonNull Beer v1, @NonNull Beer v2) {
        // Double-overwrite to avoid modifying the original values.
        // Beer could implement a clone method instead.
        Beer newValue = new Beer();
        newValue.overwrite(v1);
        newValue.overwrite(v2);

        return newValue;
    }
}
