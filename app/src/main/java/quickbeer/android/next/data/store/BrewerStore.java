/**
 * This file is part of QuickBrewer.
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
import android.support.v4.util.Pair;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.schematicprovider.BrewerColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.pojo.Brewer;
import quickbeer.android.next.rx.NullFilter;
import quickbeer.android.next.utils.DateUtils;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BrewerStore extends StoreBase<Brewer, Integer> {
    private static final String TAG = BrewerStore.class.getSimpleName();

    public BrewerStore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver, gson);
    }

    public Observable<List<Integer>> getAccessedBrewerIds() {
        return Observable.just(null)
                .observeOn(Schedulers.io())
                .map(empty -> {
                    String[] projection = new String[]{ BrewerColumns.ID, BrewerColumns.ACCESSED };
                    String selection = String.format("%s > 0", BrewerColumns.ACCESSED); // Has access date
                    String orderBy = String.format("%s DESC", BrewerColumns.ACCESSED); // Sort by date

                    return getContentResolver().query(getContentUri(), projection, selection, null, orderBy);
                })
                .filter(new NullFilter())
                .map(cursor -> {
                    List<Pair<Integer, Integer>> brewerIds = new ArrayList<>();
                    if (cursor.moveToFirst()) {
                        do {
                            int id = cursor.getInt(cursor.getColumnIndex(BrewerColumns.ID));
                            int accessed = cursor.getInt(cursor.getColumnIndex(BrewerColumns.ACCESSED));
                            brewerIds.add(new Pair<>(id, accessed));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    return brewerIds;
                })
                .observeOn(Schedulers.computation())
                .doOnNext(dataPair -> Log.d(TAG, "Accessed brewers: " + dataPair.size()))
                .map(dataPair -> {
                    List<Integer> ids = new ArrayList<>();
                    for (Pair<Integer, Integer> idAccessPair : dataPair) {
                        ids.add(idAccessPair.first);
                    }
                    return ids;
                });
    }

    public Observable<Integer> getNewlyAccessedBrewerIds(Date date) {
        return getStream()
                .filter(brewer -> brewer.getAccessDate() != null)
                .distinctUntilChanged(new Func1<Brewer, Date>() {
                    // Access date as key object indicating distinction
                    private Date latestAccess = date;

                    @Override
                    public Date call(Brewer brewer) {
                        if (brewer.getAccessDate().after(latestAccess)) {
                            latestAccess = brewer.getAccessDate();
                        }
                        return latestAccess;
                    }
                })
                .map(Brewer::getId);
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull Brewer item) {
        Preconditions.checkNotNull(item, "Brewer cannot be null.");

        return item.getId();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return RateBeerProvider.Brewers.BREWERS;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] {
                BrewerColumns.ID,
                BrewerColumns.JSON,
                BrewerColumns.NAME,
                BrewerColumns.UPDATED,
                BrewerColumns.ACCESSED
        };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(Brewer item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BrewerColumns.ID, item.getId());
        contentValues.put(BrewerColumns.JSON, getGson().toJson(item));
        contentValues.put(BrewerColumns.NAME, item.getName());
        contentValues.put(BrewerColumns.UPDATED, DateUtils.toDbValue(item.getUpdateDate()));
        contentValues.put(BrewerColumns.ACCESSED, DateUtils.toDbValue(item.getAccessDate()));

        return contentValues;
    }

    @NonNull
    @Override
    protected Brewer read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BrewerColumns.JSON));
        final Date updated = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BrewerColumns.UPDATED)));
        final Date accessed = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BrewerColumns.ACCESSED)));

        Brewer brewer = getGson().fromJson(json, Brewer.class);
        brewer.setUpdateDate(updated);
        brewer.setAccessDate(accessed);

        return brewer;
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.Brewers.withId(id);
    }

    @NonNull
    @Override
    protected Brewer mergeValues(@NonNull Brewer v1, @NonNull Brewer v2) {
        Brewer newValue = new Brewer();
        newValue.overwrite(v1);
        newValue.overwrite(v2);

        return newValue;
    }
}
