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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.next.network;

import android.net.Uri;

import java.util.List;
import java.util.Map;

import okhttp3.Response;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.Brewer;
import quickbeer.android.next.pojo.Review;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface RateBeerService {
    Uri LOGIN   = Uri.parse("__login");
    Uri BEER    = Uri.parse("__beer");
    Uri SEARCH  = Uri.parse("__search");
    Uri TOP50   = Uri.parse("__top50");
    Uri COUNTRY = Uri.parse("__country");
    Uri STYLE   = Uri.parse("__style");
    Uri REVIEWS = Uri.parse("__reviews");
    Uri TICKS   = Uri.parse("__ticks");
    Uri BREWER  = Uri.parse("__brewer");

    @FormUrlEncoded
    @POST("/Signin_r.asp")
    Observable<Response> login(@Field("username") String username,
                               @Field("pwd") String password,
                               @Field("saveinfo") String saveinfo);

    @GET("/json/bff.asp")
    Observable<List<Beer>> getBeer(@QueryMap Map<String, String> params);

    @GET("/json/bff.asp")
    Observable<List<Beer>> search(@QueryMap Map<String, String> params);

    @GET("/json/tb.asp")
    Observable<List<Beer>> topBeers(@QueryMap Map<String, String> params);

    @GET("/json/style.asp")
    Observable<List<Beer>> beersInStyle(@QueryMap Map<String, String> params);

    @GET("/json/gr.asp")
    Observable<List<Review>> getReviews(@QueryMap Map<String, String> params);

    @GET("/json/bt.asp")
    Observable<List<Beer>> getTicks(@QueryMap Map<String, String> params);

    @GET("/json/bi.asp")
    Observable<List<Brewer>> getBrewer(@QueryMap Map<String, String> params);
}
