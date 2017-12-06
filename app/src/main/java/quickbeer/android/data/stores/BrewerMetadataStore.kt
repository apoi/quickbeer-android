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
import io.reark.reark.data.stores.DefaultStore.*
import polanski.option.Option
import quickbeer.android.data.columns.BrewerMetadataColumns
import quickbeer.android.data.pojos.BrewerMetadata
import quickbeer.android.data.stores.cores.BrewerMetadataStoreCore
import quickbeer.android.data.stores.cores.CachingStoreCore

class BrewerMetadataStore(contentResolver: ContentResolver, gson: Gson)
    : StoreBase<Int, BrewerMetadata, Option<BrewerMetadata>>(
        CachingStoreCore(BrewerMetadataStoreCore(contentResolver, gson), Function { it.brewerId }, BiFunction { v1, v2 -> BrewerMetadata.merge(v1, v2) }),
        GetIdForItem { it.brewerId },
        GetNullSafe { Option.ofObj(it) },
        GetEmptyValue { Option.none<BrewerMetadata>() }) {

    fun getAccessedIdsOnce(): Observable<List<Int>> {
        val core = providerCore as BrewerMetadataStoreCore
        return core.getAccessedIdsOnce(BrewerMetadataColumns.ID, BrewerMetadataColumns.ACCESSED)
    }
}
