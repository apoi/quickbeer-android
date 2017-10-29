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
package quickbeer.android.data.stores

import io.reark.reark.data.stores.DefaultStore
import quickbeer.android.data.stores.cores.CachingStoreCore
import quickbeer.android.data.stores.cores.StoreCoreBase

open class StoreBase<T, U, R>(val core: CachingStoreCore<T, U>,
                              getIdForItem: DefaultStore.GetIdForItem<T, U>,
                              getNullSafe: DefaultStore.GetNullSafe<U, R>,
                              getEmptyValue: DefaultStore.GetEmptyValue<R>)
    : DefaultStore<T, U, R>(core, getIdForItem, getNullSafe, getEmptyValue) {

    val providerCore: StoreCoreBase<T, U>
        get() = core.providerCore
}
