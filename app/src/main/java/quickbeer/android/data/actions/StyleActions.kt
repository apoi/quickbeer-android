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

package quickbeer.android.data.actions

import io.reark.reark.data.DataStreamNotification
import polanski.option.Option
import quickbeer.android.data.pojos.BeerStyle
import quickbeer.android.data.pojos.ItemList
import rx.Observable
import rx.Single

interface StyleActions {
    fun get(styleId: Int): Single<Option<BeerStyle>>
    fun beers(styleId: Int): Observable<DataStreamNotification<ItemList<String>>>
    // TODO implement
    // fun fetchBeers(styleId: Int): Single<Boolean>
}