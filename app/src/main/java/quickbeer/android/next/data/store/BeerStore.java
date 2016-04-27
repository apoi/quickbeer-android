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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.schematicprovider.BeerColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.rx.NullFilter;
import quickbeer.android.next.utils.DateUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

public class BeerStore extends AccessTrackingStore<Beer> {
    private static final String TAG = BeerStore.class.getSimpleName();

    public BeerStore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver, gson);
    }

    public Observable<List<Integer>> getAccessedIds() {
        return super.getAccessedIds(BeerColumns.ID, BeerColumns.ACCESSED);
    }

    public Observable<Integer> getNewlyAccessedIds(Date date) {
        return getNewlyAccessedItems(date)
                .map(Beer::getId);
    }

    public Observable<List<Integer>> getTickedIds() {
        return Observable.just(null)
                .observeOn(Schedulers.io())
                .map(empty -> {
                    String[] projection = new String[]{ BeerColumns.ID };
                    String selection = String.format("%s > 0", BeerColumns.TICK_VALUE); // Has access date
                    String orderBy = String.format("%s DESC", BeerColumns.TICK_DATE); // Sort by latest ticked

                    return getContentResolver().query(getContentUri(), projection, selection, null, orderBy);
                })
                .filter(new NullFilter())
                .map(cursor -> {
                    List<Integer> idList = new ArrayList<>();
                    if (cursor.moveToFirst()) {
                        do {
                            idList.add(cursor.getInt(cursor.getColumnIndex(BeerColumns.ID)));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    return idList;
                })
                .observeOn(Schedulers.computation())
                .doOnNext(idList -> Log.d(TAG, "Ticked beers: " + idList.size()));
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
                BeerColumns.TICK_VALUE,
                BeerColumns.TICK_DATE,
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
        contentValues.put(BeerColumns.TICK_VALUE, item.getTickValue());
        contentValues.put(BeerColumns.TICK_DATE, DateUtils.toDbValue(item.getTickDate()));
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
        final int tickValue = cursor.getInt(cursor.getColumnIndex(BeerColumns.TICK_VALUE));
        final Date tickDate = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BeerColumns.TICK_DATE)));
        final int reviewId = cursor.getInt(cursor.getColumnIndex(BeerColumns.REVIEW));
        final boolean isModified = cursor.getInt(cursor.getColumnIndex(BeerColumns.MODIFIED)) > 0;
        final Date updated = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BeerColumns.UPDATED)));
        final Date accessed = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BeerColumns.ACCESSED)));

        Beer beer = getGson().fromJson(json, Beer.class);
        beer.setTickValue(tickValue);
        beer.setTickDate(tickDate);
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
