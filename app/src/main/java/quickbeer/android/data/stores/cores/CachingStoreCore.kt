/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.data.stores.cores

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.reark.reark.data.stores.cores.MemoryStoreCore
import io.reark.reark.data.stores.interfaces.StoreCoreInterface

class CachingStoreCore<T, U>(
    val providerCore: StoreCoreBase<T, U>,
    getIdForItem: Function<U, T>,
    mergeFunction: BiFunction<U, U, U>
) : StoreCoreInterface<T, U> {

    private val memoryCore: MemoryStoreCore<T, U> = MemoryStoreCore<T, U>(mergeFunction)

    init {
        // Subscribe to all updates to keep cache up-to-date
        providerCore.stream
            .subscribeOn(Schedulers.io())
            .subscribe { item -> memoryCore.put(getIdForItem.apply(item), item) }
    }

    override fun put(id: T, item: U): Single<Boolean> {
        return providerCore.put(id, item)
    }

    override fun delete(id: T): Single<Boolean> {
        return memoryCore.delete(id)
            .flatMap { providerCore.delete(id) }
    }

    override fun getCached(id: T): Maybe<U> {
        return memoryCore.getCached(id)
            .switchIfEmpty(providerCore.getCached(id)
                .doOnSuccess { memoryCore.put(id, it) })
    }

    override fun getCached(): Single<List<U>> {
        // This could also cache the results?
        return providerCore.cached
    }

    override fun getStream(id: T): Observable<U> {
        return providerCore.getStream(id)
    }

    override fun getStream(): Observable<U> {
        return providerCore.stream
    }
}
