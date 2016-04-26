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
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reark.reark.utils.Log;
import quickbeer.android.next.pojo.base.AccessTrackingItem;
import quickbeer.android.next.rx.NullFilter;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Abstract store that has some convenience methods for returning recently accessed items and ids.
 */
public abstract class AccessTrackingStore<T extends AccessTrackingItem> extends StoreBase<T, Integer> {
    private static final String TAG = AccessTrackingStore.class.getSimpleName();
    
    public AccessTrackingStore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver, gson);
    }

    protected Observable<List<Integer>> getAccessedIds(String idColumn, String accessColumn) {
        return Observable.just(null)
                .observeOn(Schedulers.io())
                .map(empty -> {
                    String[] projection = new String[]{ idColumn, accessColumn };
                    String selection = String.format("%s > 0", accessColumn); // Has access date
                    String orderBy = String.format("%s DESC", accessColumn); // Sort by date

                    return getContentResolver().query(getContentUri(), projection, selection, null, orderBy);
                })
                .filter(new NullFilter())
                .map(cursor -> {
                    List<Pair<Integer, Integer>> itemIds = new ArrayList<>();
                    if (cursor.moveToFirst()) {
                        do {
                            int id = cursor.getInt(cursor.getColumnIndex(idColumn));
                            int accessed = cursor.getInt(cursor.getColumnIndex(accessColumn));
                            itemIds.add(new Pair<>(id, accessed));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    return itemIds;
                })
                .observeOn(Schedulers.computation())
                .doOnNext(dataPair -> Log.d(TAG, "Accessed items: " + dataPair.size()))
                .map(dataPair -> {
                    List<Integer> ids = new ArrayList<>();
                    for (Pair<Integer, Integer> idAccessPair : dataPair) {
                        ids.add(idAccessPair.first);
                    }
                    return ids;
                });
    }

    protected Observable<T> getNewlyAccessedItems(Date date) {
        return getStream()
                .filter(item -> item.getAccessDate() != null)
                .distinctUntilChanged(new Func1<T, Date>() {
                    // Access date as key object indicating distinction
                    private Date latestAccess = date;

                    @Override
                    public Date call(T item) {
                        if (item.getAccessDate().after(latestAccess)) {
                            latestAccess = item.getAccessDate();
                        }
                        return latestAccess;
                    }
                });
    }
}
