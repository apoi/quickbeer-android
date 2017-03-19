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
package quickbeer.android.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;

import java.util.Map;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import okhttp3.ResponseBody;
import quickbeer.android.Constants;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.User;
import quickbeer.android.data.stores.UserStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.NetworkUtils;
import quickbeer.android.rx.RxUtils;
import rx.Single;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class TickBeerFetcher extends FetcherBase<Uri> {

    @NonNull
    private final NetworkApi networkApi;

    @NonNull
    private final NetworkUtils networkUtils;

    @NonNull
    private final UserStore userStore;

    public TickBeerFetcher(@NonNull NetworkApi networkApi,
                           @NonNull NetworkUtils networkUtils,
                           @NonNull Action1<NetworkRequestStatus> networkRequestStatus,
                           @NonNull UserStore userStore) {
        super(networkRequestStatus);

        this.networkApi = get(networkApi);
        this.networkUtils = get(networkUtils);
        this.userStore = get(userStore);
    }

    @Override
    public void fetch(@NonNull Intent intent) {
        int beerId = get(intent).getIntExtra("beerId", -1);
        int rating = get(intent).getIntExtra("rating", -1);

        if (beerId < 0 || rating < 0) {
            Timber.e("Invalid extras: beerId(%s), rating(%s)", beerId, rating);
            return;
        }

        final String uri = getUniqueUri(beerId);
        final int requestId = uri.hashCode();

        if (isOngoingRequest(requestId)) {
            Timber.d("Found an ongoing request for beer tick");
            return;
        }

        Timber.d("Tick rating of %s for beer %s", rating, beerId);

        Subscription subscription = userStore
                .getOnce(Constants.DEFAULT_USER_ID)
                .compose(RxUtils::valueOrError)
                .map(User::id)
                .toSingle()
                .flatMap(userId -> createNetworkObservable(beerId, rating, userId))
                .subscribeOn(Schedulers.computation())
                .doOnSubscribe(() -> startRequest(uri))
                .doOnSuccess(updated -> completeRequest(uri, false))
                .doOnError(doOnError(uri))
                .subscribe(RxUtils::nothing,
                        error -> Timber.e(error, "Error ticking beer"));

        addRequest(requestId, subscription);
    }

    @NonNull
    private Single<ResponseBody> createNetworkObservable(int beerId, int rating, int userId) {
        Map<String, String> requestParams = networkUtils.createRequestParams("m", "2");
        requestParams.put("b", String.valueOf(beerId));
        requestParams.put("l", String.valueOf(rating));
        requestParams.put("u", String.valueOf(userId));

        return networkApi.tickBeer(requestParams);
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.TICK;
    }

    @NonNull
    public static String getUniqueUri(int id) {
        return Beer.class + "/" + id + "/rate";
    }
}
