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
package quickbeer.android.features.countrydetails

import io.reark.reark.data.DataStreamNotification
import polanski.option.Option
import quickbeer.android.data.DataLayer
import quickbeer.android.data.pojos.Country
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.providers.ProgressStatusProvider
import quickbeer.android.viewmodels.BeerListViewModel
import rx.Observable
import rx.Single
import javax.inject.Inject
import javax.inject.Named

class CountryViewModel @Inject
internal constructor(@Named("id") private val countryId: Int,
                     private val getCountry: DataLayer.GetCountry,
                     private val getBeersInCountry: DataLayer.GetBeersInCountry,
                     getBeer: DataLayer.GetBeer,
                     progressStatusProvider: ProgressStatusProvider)
    : BeerListViewModel(getBeer, progressStatusProvider) {

    fun getCountry(): Single<Option<Country>> {
        return getCountry.call(countryId.toInt());
    }

    override fun sourceObservable(): Observable<DataStreamNotification<ItemList<String>>> {
        return getBeersInCountry.call(countryId.toString())
    }
}