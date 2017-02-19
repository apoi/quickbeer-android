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

import android.support.annotation.NonNull;

import io.reark.reark.data.stores.cores.MemoryStoreCore;
import io.reark.reark.data.stores.interfaces.StoreCoreInterface;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static io.reark.reark.utils.Preconditions.get;

public class CachingStoreCore<T, U> implements StoreCoreInterface<T, U> {

    @NonNull
    private final StoreCoreBase<T, U> providerCore;

    @NonNull
    private final MemoryStoreCore<T, U> memoryCore;

    public CachingStoreCore(@NonNull StoreCoreBase<T, U> providerCore,
                            @NonNull MemoryStoreCore<T, U> memoryCore,
                            @NonNull Func1<U, T> getIdForItem) {
        this.providerCore = get(providerCore);
        this.memoryCore = get(memoryCore);

        // Subscribe to all updates to keep cache up-to-date
        providerCore.getAllStream()
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.io())
                .subscribe(item -> memoryCore.put(getIdForItem.call(item), item));
    }

    @NonNull
    public StoreCoreBase<T, U> getProviderCore() {
        return providerCore;
    }

    @Override
    public void put(@NonNull T id, @NonNull U item) {
        providerCore.put(id, item);
    }

    @NonNull
    @Override
    public Observable<U> getCached(@NonNull T id) {
        return memoryCore.getCached(id)
                .subscribeOn(Schedulers.io())
                .switchIfEmpty(providerCore
                        .getCached(id)
                        .doOnNext(item -> memoryCore.put(id, item)));
    }

    @NonNull
    @Override
    public Observable<U> getStream(@NonNull T id) {
        return providerCore.getStream(id);
    }
}
