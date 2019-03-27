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

import android.content.ContentResolver
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reark.reark.data.stores.DefaultStore.GetEmptyValue
import io.reark.reark.data.stores.DefaultStore.GetIdForItem
import io.reark.reark.data.stores.DefaultStore.GetNullSafe
import polanski.option.Option
import quickbeer.android.data.columns.BeerMetadataColumns
import quickbeer.android.data.pojos.BeerMetadata
import quickbeer.android.data.stores.cores.BeerMetadataStoreCore
import quickbeer.android.data.stores.cores.CachingStoreCore

class BeerMetadataStore(contentResolver: ContentResolver, gson: Gson) :
    StoreBase<Int, BeerMetadata, Option<BeerMetadata>>(
        CachingStoreCore(
            BeerMetadataStoreCore(contentResolver, gson),
            Function { it.beerId },
            BiFunction { v1, v2 -> BeerMetadata.merge(v1, v2) }),
        GetIdForItem { it.beerId },
        GetNullSafe { Option.ofObj(it) },
        GetEmptyValue { Option.none<BeerMetadata>() }) {

    // This isn't a result set but rather a custom query. Thus, no caching.
    fun getAccessedIdsOnce(): Observable<List<Int>> {
        val core = providerCore as BeerMetadataStoreCore
        return core.getAccessedIdsOnce(BeerMetadataColumns.ID, BeerMetadataColumns.ACCESSED)
    }
}
