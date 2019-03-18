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
package quickbeer.android.data.pojos

import com.google.gson.annotations.SerializedName
import org.threeten.bp.ZonedDateTime
import polanski.option.Option.ofObj
import quickbeer.android.Constants
import quickbeer.android.data.pojos.base.Overwritable
import quickbeer.android.utils.kotlin.hasValue
import java.util.Locale

data class Beer(
    @SerializedName("BeerID") val id: Int,
    @SerializedName("BeerName") val name: String?,
    @SerializedName("BrewerID") val brewerId: Int?,
    @SerializedName("BrewerName") val brewerName: String?,
    @SerializedName("ContractBrewerID") val contractBrewerId: Int?,
    @SerializedName("ContractBrewer") val contractBrewerName: String?,
    @SerializedName("AverageRating") val averageRating: Float?,
    @SerializedName("OverallPctl") val overallRating: Float?,
    @SerializedName("StylePctl") val styleRating: Float?,
    @SerializedName("RateCount") val rateCount: Int?,
    @SerializedName("BrewerCountryId") val countryId: Int?,
    @SerializedName("BeerStyleID") val styleId: Int?,
    @SerializedName("BeerStyleName") val styleName: String?,
    @SerializedName("Alcohol") val alcohol: Float?,
    @SerializedName("IBU") val ibu: Float?,
    @SerializedName("Description") val description: String?,
    @SerializedName("IsAlias") val isAlias: Boolean?,
    @SerializedName("Retired") val isRetired: Boolean?,
    @SerializedName("Verified") val isVerified: Boolean?,
    @SerializedName("Unrateable") val unrateable: Boolean?,
    @SerializedName("Liked") val tickValue: Int?,
    @SerializedName("TimeEntered") val tickDate: ZonedDateTime?
) : Overwritable<Beer>() {

    fun basicDataMissing(): Boolean {
        return brewerId == null || !styleName.hasValue()
    }

    fun detailedDataMissing(): Boolean {
        return basicDataMissing() || description == null
    }

    fun rating(): Int {
        return ofObj(overallRating)
            .filter { value -> value > 0.0 }
            .map { Math.round(it) }
            .orDefault { -1 }
    }

    fun imageUri(): String {
        return String.format(Locale.ROOT, Constants.BEER_IMAGE_PATH, id)
    }

    fun isTicked(): Boolean {
        return ofObj(tickValue).orDefault { -1 } > 0
    }

    override fun getTypeParameterClass(): Class<Beer> {
        return Beer::class.java
    }

    companion object {
        fun merge(v1: Beer, v2: Beer): Beer {
            val beer = v1.copy()
            beer.overwrite(v2)
            return beer
        }
    }
}
