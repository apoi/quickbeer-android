/**
 * This file is part of QuickBeer.
 * Copyright (C) 2024 Antti Poikela <antti.poikela@iki.fi>
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
 * Rating made by the currently logged in user. We know user details, details of
 * the beer are included in the response.
 */
data class UsersRatingJson(
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

    // Beer data
    @field:Json(name = "BeerID") val beerId: Int,
    @field:Json(name = "BeerName") val beerName: String,
    @field:Json(name = "BeerStyleID") val beerStyleId: Int,
    @field:Json(name = "BeerStyleName") val beerStyleName: String,
    @field:Json(name = "BrewerID") val brewerId: Int,
    @field:Json(name = "BrewerName") val brewerName: String,
    @field:Json(name = "AverageRating") val averageRating: Float,
    @field:Json(name = "OverallPctl") val overallPercentile: Float?,
    @field:Json(name = "StylePctl") val stylePercentile: Float?,
    @field:Json(name = "RateCount") val rateCount: Int
)
