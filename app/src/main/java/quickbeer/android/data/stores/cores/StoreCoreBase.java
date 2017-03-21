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
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import io.reark.reark.data.stores.cores.ContentProviderStoreCore;
import quickbeer.android.data.providers.RateBeerProvider;

public abstract class StoreCoreBase<T, U> extends ContentProviderStoreCore<T, U> {

    @NonNull
    private final Gson gson;

    protected StoreCoreBase(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        super(contentResolver);

        this.gson = gson;
    }

    @Override
    @NonNull
    protected String getAuthority() {
        return RateBeerProvider.AUTHORITY;
    }

    @NonNull
    protected Gson getGson() {
        return gson;
    }
}
