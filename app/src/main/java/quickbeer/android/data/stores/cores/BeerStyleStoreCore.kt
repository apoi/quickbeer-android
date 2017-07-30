/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela></antti.poikela>@iki.fi>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.data.stores.cores

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reark.reark.data.stores.cores.MemoryStoreCore
import ix.Ix
import quickbeer.android.R
import quickbeer.android.data.pojos.BeerStyle
import quickbeer.android.providers.ResourceProvider
import timber.log.Timber
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import javax.inject.Inject

class BeerStyleStoreCore @Inject
internal constructor(resourceProvider: ResourceProvider,
                     gson: Gson)
    : MemoryStoreCore<Int, BeerStyle>() {

    init {
        try {
            val input = resourceProvider.openRawResource(R.raw.styles)
            val reader = InputStreamReader(input, "UTF-8")

            val listType = object : TypeToken<ArrayList<BeerStyle>>() {}.type
            val styleList = gson.fromJson<List<BeerStyle>>(reader, listType)

            reader.close()
            input.close()

            Ix.from(styleList)
                    .subscribe { beerStyle -> put(beerStyle.id, beerStyle) }

        } catch (e: IOException) {
            Timber.e(e, "Failed reading styles!")
        }
    }
}
