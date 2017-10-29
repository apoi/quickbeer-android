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
import io.reark.reark.data.stores.DefaultStore.*
import polanski.option.Option
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.data.stores.cores.CachingStoreCore
import quickbeer.android.data.stores.cores.ReviewListStoreCore
import rx.functions.Func1
import rx.functions.Func2

/**
 * Class storing lists of reviews related to a specific beer id.
 */
class ReviewListStore(contentResolver: ContentResolver, gson: Gson)
    : StoreBase<Int, ItemList<Int>, Option<ItemList<Int>>>(
        CachingStoreCore(ReviewListStoreCore(contentResolver, gson), Func1 { it.key }, Func2 { _, s -> s }),
        GetIdForItem { it.key },
        GetNullSafe { Option.ofObj(it) },
        GetEmptyValue { Option.none<ItemList<Int>>() })
