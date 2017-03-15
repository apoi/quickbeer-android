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

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

import quickbeer.android.data.columns.BeerColumns;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.providers.RateBeerProvider;
import quickbeer.android.utils.DateUtils;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class BeerStoreCore extends StoreCoreBase<Integer, Beer> {

    public BeerStoreCore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    public Observable<List<Integer>> queryTicks() {
        return Observable
                .fromCallable(() -> {
                    String[] projection = { BeerColumns.ID };
                    String selection = String.format("%s > 0", BeerColumns.TICK_VALUE); // Has tick value
                    String orderBy = String.format("%s DESC", BeerColumns.TICK_DATE); // Sort by latest ticked

                    List<Integer> idList = new ArrayList<>(10);

                    final Cursor cursor = getContentResolver().query(getContentUri(), projection, selection, null, orderBy);

                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            do {
                                idList.add(cursor.getInt(cursor.getColumnIndex(BeerColumns.ID)));
                            } while (cursor.moveToNext());
                        }
                        cursor.close();
                    }

                    return idList;
                })
                .doOnNext(idList -> Timber.d("Ticked beers: " + idList.size()))
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    protected Uri getUriForId(@NonNull Integer id) {
        return RateBeerProvider.Beers.withId(get(id));
    }

    @NonNull
    @Override
    protected Integer getIdForUri(@NonNull Uri uri) {
        return RateBeerProvider.Beers.fromUri(get(uri));
    }

    @NonNull
    @Override
    protected Uri getContentUri() {
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
        };
    }

    @NonNull
    @Override
    protected Beer read(@NonNull Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BeerColumns.JSON));
        final int tickValue = cursor.getInt(cursor.getColumnIndex(BeerColumns.TICK_VALUE));
        final ZonedDateTime tickDate = DateUtils.fromEpochSecond(cursor.getInt(cursor.getColumnIndex(BeerColumns.TICK_DATE)));

        return Beer.builder(Beer.fromJson(json, getGson()))
                .tickValue(tickValue)
                .tickDate(tickDate)
                .build();
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull Beer item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BeerColumns.ID, item.id());
        contentValues.put(BeerColumns.JSON, getGson().toJson(item));
        contentValues.put(BeerColumns.NAME, item.name());
        contentValues.put(BeerColumns.TICK_VALUE, item.tickValue());
        contentValues.put(BeerColumns.TICK_DATE, DateUtils.toEpochSecond(item.tickDate()));

        return contentValues;
    }

    @NonNull
    @Override
    protected Beer mergeValues(@NonNull Beer v1, @NonNull Beer v2) {
        return Beer.merge(v1, v2);
    }
}
