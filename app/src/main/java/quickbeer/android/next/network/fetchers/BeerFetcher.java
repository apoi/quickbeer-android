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
package quickbeer.android.next.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.utils.NetworkUtils;
import quickbeer.android.next.pojo.Beer;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class BeerFetcher extends FetcherBase {
    private static final String TAG = BeerFetcher.class.getSimpleName();

    private final NetworkApi networkApi;
    private final NetworkUtils networkUtils;
    private final BeerStore beerStore;

    public BeerFetcher(@NonNull NetworkApi networkApi,
                       @NonNull NetworkUtils networkUtils,
                       @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                       @NonNull BeerStore beerStore) {
        super(updateNetworkRequestStatus);

        Preconditions.checkNotNull(networkApi, "Network API cannot be null.");
        Preconditions.checkNotNull(networkUtils, "Network utils cannot be null.");
        Preconditions.checkNotNull(beerStore, "Beer store cannot be null.");

        this.networkApi = networkApi;
        this.networkUtils = networkUtils;
        this.beerStore = beerStore;
    }

    @Override
    public void fetch(Intent intent) {
        Preconditions.checkNotNull(intent, "Fetch intent cannot be null.");

        final int beerId = intent.getIntExtra("id", -1);
        if (beerId != -1) {
            fetchBeer(beerId);
        } else {
            Log.e(TAG, "No beerId provided in the intent extras");
        }
    }

    private void fetchBeer(final int beerId) {
        Log.d(TAG, "fetchBeer(" + beerId + ")");

        if (requestMap.containsKey(beerId) && !requestMap.get(beerId).isUnsubscribed()) {
            Log.d(TAG, "Found an ongoing request for beer " + beerId);
            return;
        }

        final String uri = beerStore.getUriForId(beerId).toString();
        Subscription subscription = createNetworkObservable(beerId)
                .subscribeOn(Schedulers.computation())
                .doOnError(doOnError(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .subscribe(beerStore::put,
                        e -> Log.e(TAG, "Error fetching beer " + beerId, e));

        requestMap.put(beerId, subscription);
        startRequest(uri);
    }

    @NonNull
    private Observable<Beer> createNetworkObservable(int beerId) {
        return networkApi.getBeer(networkUtils.createRequestParams("bd", String.valueOf(beerId)));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.BEER;
    }
}
