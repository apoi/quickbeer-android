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

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.Brewer;
import quickbeer.android.next.pojo.Review;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import rx.Observable;

public class NetworkApi {

    private final RateBeerService rateBeerService;

    public NetworkApi(@NonNull Client client, @NonNull Gson gson) {
        Preconditions.checkNotNull(client, "Client cannot be null.");
        Preconditions.checkNotNull(client, "Gson cannot be null.");

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint("https://www.ratebeer.com")
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();

        rateBeerService = restAdapter.create(RateBeerService.class);
    }

    public Observable<Response> login(String username, String password) {
        return rateBeerService
                .login(username, password, "on");
    }

    public Observable<Beer> getBeer(Map<String, String> params) {
        return rateBeerService
                .getBeer(params)
                .map(list -> list.get(0)); // API returns a list of one beer
    }

    public Observable<List<Beer>> search(Map<String, String> params) {
        return rateBeerService
                .search(params);
    }

    public Observable<List<Beer>> getBeersInCountry(Map<String, String> params) {
        return rateBeerService
                .topBeers(params);
    }

    public Observable<List<Beer>> getBeersInStyle(Map<String, String> params) {
        return rateBeerService
                .beersInStyle(params);
    }

    public Observable<List<Review>> getReviews(Map<String, String> params) {
        return rateBeerService
                .getReviews(params);
    }

    public Observable<Brewer> getBrewer(Map<String, String> params) {
        return rateBeerService
                .getBrewer(params)
                .map(list -> list.get(0)); // API returns a list of one brewer
    }
}
