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
package quickbeer.android.domain.rating.network

import com.squareup.moshi.Json
import org.threeten.bp.ZonedDateTime

/**
 * Generic rating of a given beer. We know beer details, details of
 * the user are included in the response.
 */
data class RatingJson(
    // Rating data
    @field:Json(name = "RatingID") val id: Int,
    @field:Json(name = "Appearance") val appearance: Int?,
    @field:Json(name = "Aroma") val aroma: Int?,
    @field:Json(name = "Flavor") val flavor: Int?,
    @field:Json(name = "Mouthfeel") val mouthfeel: Int?,
    @field:Json(name = "Overall") val overall: Int?,
    @field:Json(name = "TotalScore") val totalScore: Float?,
    @field:Json(name = "Comments") val comments: String?,
    @field:Json(name = "TimeEntered") val timeEntered: ZonedDateTime?,
    @field:Json(name = "TimeUpdated") val timeUpdated: ZonedDateTime?,

    // User data
    @field:Json(name = "UserID") val userId: Int?,
    @field:Json(name = "UserName") val userName: String?,
    @field:Json(name = "City") val city: String?,
    @field:Json(name = "StateID") val stateId: Int?,
    @field:Json(name = "State") val state: String?,
    @field:Json(name = "CountryID") val countryId: Int?,
    @field:Json(name = "Country") val country: String?,
    @field:Json(name = "RateCount") val rateCount: Int?
)
