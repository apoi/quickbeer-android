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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.reark.reark.data.store.SingleItemContentProviderStore;
import io.reark.reark.utils.Log;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Caching layer on top of SingleItemContentProviderStore. We get notifications of item value
 * changes via the general stream, so it's easy to do an initial request to the backing content
 * provider and cache the values for all subsequent calls. This helps especially with quick
 * repeated data requests, such as when scrolling a list of items back and forth.
 *
 * @param <T> Type of the data this store contains.
 * @param <U> Type of the id used in this store.
 */
public abstract class StoreBase<T, U> extends SingleItemContentProviderStore<T, U> {
    private static final String TAG = StoreBase.class.getSimpleName();

    private final Gson gson;
    private final ConcurrentHashMap<U, T> cache = new ConcurrentHashMap<>();

    private final boolean cacheOnly;
    private boolean hasRequestedFullStore = false;

    public StoreBase(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        this(contentResolver, gson, false);
    }

    public StoreBase(@NonNull ContentResolver contentResolver, @NonNull Gson gson, boolean cacheOnly) {
        super(contentResolver);

        this.gson = gson;
        this.cacheOnly = cacheOnly;

        // Cache all additions and updates to values
        getStream()
                .subscribeOn(Schedulers.computation())
                .subscribe(this::cacheItem);
    }

    @NonNull
    @Override
    public Observable<List<T>> get(@NonNull U key) {
        if (!hasRequestedFullStore && !cacheOnly) {
            return super.get(key)
                    .observeOn(Schedulers.computation())
                    .doOnNext(this::cacheItems)
                    .doOnNext(items -> hasRequestedFullStore = true)
                    .doOnError(throwable -> Log.e(TAG, "error", throwable));
        } else {
            return Observable.just(key)
                    .subscribeOn(Schedulers.computation())
                    .map(u -> new ArrayList<>(cache.values()));
        }
    }

    @NonNull
    @Override
    public Observable<T> getOne(@NonNull U key) {
        return Observable.just(key)
                .subscribeOn(Schedulers.computation())
                .map(cache::get)
                .flatMap(item -> item == null && !cacheOnly
                        ? super.getOne(key).doOnNext(this::cacheItem)
                        : Observable.just(item)
                )
                .doOnError(throwable -> Log.e(TAG, "error", throwable));
    }

    protected Gson getGson() {
        return gson;
    }

    private void cacheItem(T item) {
        if (item != null) {
            cache.put(getIdFor(item), item);
        }
    }

    private void cacheItems(List<T> items) {
        if (items != null) {
            for (T value : items) {
                cacheItem(value);
            }
        }
    }
}
