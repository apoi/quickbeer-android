/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
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
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.network

import io.reactivex.Single
import okhttp3.ResponseBody
import quickbeer.android.data.pojos.Beer
import quickbeer.android.data.pojos.Brewer
import quickbeer.android.data.pojos.Review
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface RateBeerService {

    @FormUrlEncoded
    @POST("/Signin_r.asp")
    fun login(
        @Field("username") username: String,
        @Field("pwd") password: String,
        @Field("saveinfo") saveinfo: String
    ): Single<ResponseBody>

    @GET("/json/bff.asp")
    fun getBeer(@QueryMap params: Map<String, String>): Single<List<Beer>>

    @GET("/json/bff.asp")
    fun search(@QueryMap params: Map<String, String>): Single<List<Beer>>

    @GET("/json/upc.asp")
    fun barcode(@QueryMap params: Map<String, String>): Single<List<Beer>>

    @GET("/json/tb.asp")
    fun topBeers(@QueryMap params: Map<String, String>): Single<List<Beer>>

    @GET("/json/style.asp")
    fun beersInStyle(@QueryMap params: Map<String, String>): Single<List<Beer>>

    @GET("/json/gr.asp")
    fun getReviews(@QueryMap params: Map<String, String>): Single<List<Review>>

    @GET("/json/revs.asp")
    fun getUserReviews(@QueryMap params: Map<String, String>): Single<List<Review>>

    @GET("/json/bt.asp")
    fun getTicks(@QueryMap params: Map<String, String>): Single<List<Beer>>

    @GET("/json/bt.asp")
    fun tickBeer(@QueryMap params: Map<String, String>): Single<ResponseBody>

    @GET("/json/bi.asp")
    fun getBrewer(@QueryMap params: Map<String, String>): Single<List<Brewer>>

    @GET("/json/bw.asp")
    fun getBrewerBeers(@QueryMap params: Map<String, String>): Single<List<Beer>>
}
