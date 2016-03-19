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

import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.Review;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

public interface RateBeerService {
    static Uri BEER = Uri.parse("ratebeer/beer");
    static Uri SEARCH = Uri.parse("ratebeer/search");
    static Uri TOP50 = Uri.parse("ratebeer/top50");
    static Uri REVIEWS = Uri.parse("ratebeer/reviews");
    static Uri COUNTRY = Uri.parse("ratebeer/country");

    @GET("/json/bff.asp")
    Observable<List<Beer>> getBeer(@QueryMap Map<String, String> params);

    @GET("/json/bff.asp")
    Observable<List<Beer>> search(@QueryMap Map<String, String> params);

    @GET("/json/tb.asp")
    Observable<List<Beer>> searchTopBeers(@QueryMap Map<String, String> params);

    @GET("/json/gr.asp")
    Observable<List<Review>> getReviews(@QueryMap Map<String, String> params);

    @GET("/json/bcc.asp")
    Observable<List<Beer>> getTopInCountry(@QueryMap Map<String, String> params);
}
