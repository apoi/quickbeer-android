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
package quickbeer.android.data.stores;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reark.reark.data.stores.cores.MemoryStoreCore;
import polanski.option.Option;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.stores.cores.BeerStoreCore;
import quickbeer.android.data.stores.cores.CachingStoreCore;
import rx.Observable;
import rx.schedulers.Schedulers;

public class BeerStore extends StoreBase<Integer, Beer, Option<Beer>> {

    public BeerStore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(new CachingStoreCore<>(
                        new BeerStoreCore(contentResolver, gson),
                        new MemoryStoreCore<>(Beer::merge),
                        Beer::id),
              Beer::id,
              Option::ofObj,
              Option::none);
    }

    @NonNull
    public Observable<List<Integer>> getTickedIds() {
        // Simplistic strategy of refreshing ticks list always on stream updates
        return ((BeerStoreCore) getProviderCore())
                .getAllStream()
                .filter(beer -> beer.tickDate() != null)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .flatMap(beer -> queryTicks())
                .observeOn(Schedulers.computation())
                .startWith(queryTicks())
                .distinctUntilChanged();
    }

    private Observable<List<Integer>> queryTicks() {
        return ((BeerStoreCore) getProviderCore()).queryTicks();
    }
}
