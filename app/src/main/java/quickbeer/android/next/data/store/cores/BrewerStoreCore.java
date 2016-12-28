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

import com.google.gson.Gson;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import quickbeer.android.next.data.schematicprovider.BrewerColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.Brewer;
import quickbeer.android.next.utils.DateUtils;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static io.reark.reark.utils.Preconditions.get;

public class BrewerStoreCore extends StoreCoreBase<Integer, Brewer> {

    public BrewerStoreCore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    public Observable<List<Integer>> getAccessedIds(@NonNull final String idColumn, @NonNull final String accessColumn) {
        return Observable
                .fromCallable(() -> {
                    String[] projection = { idColumn };
                    String selection = String.format("%s > 0", accessColumn); // Has access date
                    String orderBy = String.format("%s DESC", accessColumn); // Sort by date

                    List<Integer> idList = new ArrayList<>(10);

                    final Cursor cursor = getContentResolver().query(getContentUri(), projection, selection, null, orderBy);

                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            do {
                                idList.add(cursor.getInt(cursor.getColumnIndex(idColumn)));
                            } while (cursor.moveToNext());
                        }
                        cursor.close();
                    }

                    return idList;
                })
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    public Observable<Brewer> getNewlyAccessedItems(@NonNull final Date date) {
        return getAllStream()
                .filter(item -> item.getAccessDate() != null)
                .distinctUntilChanged(new Func1<Brewer, Date>() {
                    // Access date as key object indicating distinction
                    private Date latestAccess = date;

                    @Override
                    public Date call(Brewer item) {
                        if (item.getAccessDate().after(latestAccess)) {
                            latestAccess = item.getAccessDate();
                        }
                        return latestAccess;
                    }
                });
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
                BrewerColumns.NAME,
                BrewerColumns.UPDATED,
                BrewerColumns.ACCESSED
        };
    }

    @NonNull
    @Override
    protected Brewer read(@NonNull final Cursor cursor) {
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
    protected ContentValues getContentValuesForItem(@NonNull final Brewer item) {
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
    protected Brewer mergeValues(@NonNull final Brewer v1, @NonNull final Brewer v2) {
        Brewer newValue = new Brewer();
        newValue.overwrite(v1);
        newValue.overwrite(v2);

        return newValue;
    }
}
