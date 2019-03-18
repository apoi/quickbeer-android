/*
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

import com.google.gson.Gson
import io.reark.reark.data.stores.DefaultStore
import io.reark.reark.data.stores.DefaultStore.GetEmptyValue
import io.reark.reark.data.stores.DefaultStore.GetIdForItem
import io.reark.reark.data.stores.DefaultStore.GetNullSafe
import polanski.option.Option
import quickbeer.android.data.pojos.Country
import quickbeer.android.data.stores.cores.CountryStoreCore
import quickbeer.android.providers.ResourceProvider
import quickbeer.android.utils.SimpleListSource

class CountryStore(resourceProvider: ResourceProvider, gson: Gson) :
    DefaultStore<Int, Country, Option<Country>>(
        CountryStoreCore(resourceProvider, gson),
        GetIdForItem { it.id },
        GetNullSafe { Option.ofObj(it) },
        GetEmptyValue { Option.none<Country>() }),
    SimpleListSource<Country> {

    override fun getItem(id: Int): Country {
        return getOnce(id)
            .blockingGet()
            .orDefault { null }
    }

    override fun getList(): Collection<Country> {
        return getOnce()
            .blockingGet()
    }
}
