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
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reark.reark.data.stores.DefaultStore.*
import polanski.option.Option
import quickbeer.android.data.pojos.Beer
import quickbeer.android.data.stores.cores.BeerStoreCore
import quickbeer.android.data.stores.cores.CachingStoreCore

class BeerStore(contentResolver: ContentResolver, gson: Gson)
    : StoreBase<Int, Beer, Option<Beer>>(
        CachingStoreCore(BeerStoreCore(contentResolver, gson), Function { it.id }, BiFunction(){ v1, v2 -> Beer.merge(v1, v2) }),
        GetIdForItem { it.id },
        GetNullSafe { Option.ofObj(it) },
        GetEmptyValue { Option.none<Beer>() })