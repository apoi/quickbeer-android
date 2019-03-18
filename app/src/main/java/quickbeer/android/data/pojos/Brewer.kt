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
import quickbeer.android.Constants
import quickbeer.android.data.pojos.base.Overwritable
import quickbeer.android.utils.kotlin.hasValue
import java.util.Locale

data class Brewer(
    @SerializedName("BrewerID") val id: Int,
    @SerializedName("BrewerName") val name: String?,
    @SerializedName("BrewerDescription") val description: String?,
    @SerializedName("BrewerAddress") val address: String?,
    @SerializedName("BrewerCity") val city: String?,
    @SerializedName("BrewerStateID") val stateId: Int?,
    @SerializedName("BrewerCountryID") val countryId: Int?,
    @SerializedName("BrewerZipCode") val zipCode: String?,
    @SerializedName("BrewerTypeID") val typeId: Int?,
    @SerializedName("BrewerType") val type: String?,
    @SerializedName("BrewerWebSite") val website: String?,
    @SerializedName("Facebook") val facebook: String?,
    @SerializedName("Twitter") val twitter: String?,
    @SerializedName("BrewerEmail") val email: String?,
    @SerializedName("BrewerPhone") val phone: String?,
    @SerializedName("Barrels") val barrels: Int?,
    @SerializedName("Opened") val founded: ZonedDateTime?,
    @SerializedName("EnteredOn") val enteredOn: ZonedDateTime?,
    @SerializedName("EnteredBy") val enteredBy: Int?,
    @SerializedName("LogoImage") val logo: String?,
    @SerializedName("ViewCount") val viewCount: String?,
    @SerializedName("Score") val score: Int?,
    @SerializedName("OOB") val outOfBusiness: Boolean?,
    @SerializedName("retired") val retired: Boolean?,
    @SerializedName("AreaCode") val areaCode: String?,
    @SerializedName("Hours") val hours: String?,
    @SerializedName("HeadBrewer") val headBrewer: String?,
    @SerializedName("MetroID") val metroId: String?,
    @SerializedName("MSA") val msa: String?,
    @SerializedName("RegionID") val regionId: String?
) : Overwritable<Brewer>() {

    fun hasDetails(): Boolean {
        return name.hasValue()
    }

    fun getImageUri(): String {
        return String.format(Locale.ROOT, Constants.BREWER_IMAGE_PATH, id)
    }

    override fun getTypeParameterClass(): Class<Brewer> {
        return Brewer::class.java
    }

    companion object {
        fun merge(v1: Brewer, v2: Brewer): Brewer {
            val brewer = v1.copy()
            brewer.overwrite(v2)
            return brewer
        }
    }
}
