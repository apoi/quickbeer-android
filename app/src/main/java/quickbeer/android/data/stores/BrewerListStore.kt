/**
 * This file is part of QuickBrewer.
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
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.cores.BrewerListStoreCore
import quickbeer.android.data.stores.cores.CachingStoreCore

/**
 * Class storing brewer lists related to a string key, such as a search.
 */
class BrewerListStore(contentResolver: ContentResolver, gson: Gson)
    : StoreBase<String, ItemList<String>, Option<ItemList<String>>>(
        CachingStoreCore(BrewerListStoreCore(contentResolver, gson), Function { it.key }, BiFunction { _, v2 -> v2 }),
        GetIdForItem { it.key },
        GetNullSafe { Option.ofObj(it) },
        GetEmptyValue { Option.none<ItemList<String>>() })
