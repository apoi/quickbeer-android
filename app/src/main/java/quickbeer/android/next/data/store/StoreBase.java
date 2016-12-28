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

import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.data.stores.DefaultStore;
import io.reark.reark.data.stores.interfaces.StoreCoreInterface;

class StoreBase<T, U, R> extends DefaultStore<T, U, R> {

    private final StoreCoreInterface<T, U> core;

    StoreBase(@NonNull final StoreCoreInterface<T, U> core,
              @NonNull final GetIdForItem<T, U> getIdForItem,
              @NonNull final GetNullSafe<U, R> getNullSafe,
              @NonNull final GetEmptyValue<R> getEmptyValue) {
        super(core, getIdForItem, getNullSafe, getEmptyValue);

        this.core = core;
    }

    @NonNull
    StoreCoreInterface<T, U> getCore() {
        return this.core;
    }
}
