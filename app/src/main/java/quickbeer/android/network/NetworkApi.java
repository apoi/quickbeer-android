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
package quickbeer.android.network;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import quickbeer.android.Constants;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.Review;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Single;

import static io.reark.reark.utils.Preconditions.get;

public class NetworkApi {

    private final RateBeerService rateBeerService;

    public NetworkApi(@NonNull final OkHttpClient client,
                      @NonNull final Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(get(gson)))
                .baseUrl(Constants.API_URL)
                .client(get(client))
                .build();

        rateBeerService = retrofit.create(RateBeerService.class);
    }

    public Observable<Response> login(String username, String password) {
        return rateBeerService
                .login(username, password, Constants.LOGIN_DEFAULT_SAVE_INFO);
    }

    public Single<Beer> getBeer(Map<String, String> params) {
        return rateBeerService
                .getBeer(params)
                .map(list -> list.get(0)); // API returns a list of one beer
    }

    public Observable<List<Beer>> search(Map<String, String> params) {
        return rateBeerService
                .search(params);
    }

    public Observable<List<Beer>> barcode(Map<String, String> params) {
        return rateBeerService
                .barcode(params);
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

    public Observable<List<Beer>> getTicks(Map<String, String> params) {
        return rateBeerService
                .getTicks(params);
    }

    public Observable<Brewer> getBrewer(Map<String, String> params) {
        return rateBeerService
                .getBrewer(params)
                .map(list -> list.get(0)); // API returns a list of one brewer
    }
}
