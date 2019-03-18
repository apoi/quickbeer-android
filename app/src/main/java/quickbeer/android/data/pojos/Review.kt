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
import quickbeer.android.data.pojos.base.Overwritable

data class Review(
    @SerializedName("RatingID") val id: Int,
    @SerializedName("Appearance") val appearance: Int?,
    @SerializedName("Aroma") val aroma: Int?,
    @SerializedName("Flavor") val flavor: Int?,
    @SerializedName("Mouthfeel") val mouthfeel: Int?,
    @SerializedName("Overall") val overall: Int?,
    @SerializedName("TotalScore") val totalScore: Float?,
    @SerializedName("Comments") val comments: String?,
    @SerializedName("TimeEntered") val timeEntered: ZonedDateTime?,
    @SerializedName("TimeUpdated") val timeUpdated: ZonedDateTime?,
    @SerializedName("UserID") val userID: Int?,
    @SerializedName("UserName") val userName: String?,
    @SerializedName("City") val city: String?,
    @SerializedName("StateID") val stateID: Int?,
    @SerializedName("State") val state: String?,
    @SerializedName("CountryID") val countryID: Int?,
    @SerializedName("Country") val country: String?,
    @SerializedName("RateCount") val rateCount: Int?
) : Overwritable<Review>() {

    override fun getTypeParameterClass(): Class<Review> {
        return Review::class.java
    }

    companion object {
        fun merge(v1: Review, v2: Review): Review {
            val review = v1.copy()
            review.overwrite(v2)
            return review
        }
    }
}
