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

import com.google.gson.Gson
import io.reactivex.Observable
import io.reark.reark.data.stores.DefaultStore
import io.reark.reark.data.stores.DefaultStore.*
import polanski.option.Option
import quickbeer.android.data.pojos.BeerStyle
import quickbeer.android.data.stores.cores.BeerStyleStoreCore
import quickbeer.android.providers.ResourceProvider
import quickbeer.android.utils.SimpleListSource

class BeerStyleStore(resourceProvider: ResourceProvider, gson: Gson)
    : DefaultStore<Int, BeerStyle, Option<BeerStyle>>(
        BeerStyleStoreCore(resourceProvider, gson),
        GetIdForItem { it.id },
        GetNullSafe { Option.ofObj(it) },
        GetEmptyValue { Option.none<BeerStyle>() }),
        SimpleListSource<BeerStyle> {

    override fun getItem(id: Int): BeerStyle {
        return getOnce(id)
                .blockingGet()
                .orDefault { null }
    }

    override fun getList(): Collection<BeerStyle> {
        return getOnce()
                .flatMapObservable { Observable.fromIterable(it) }
                .filter { it.parent > -1 }
                .toList()
                .blockingGet()
    }

    fun getStyle(styleName: String): Option<BeerStyle> {
        return getList()
                .filter { styleName == it.name }
                .map { Option.ofObj(it) }
                .getOrElse(0, { Option.none<BeerStyle>() })
    }
}
