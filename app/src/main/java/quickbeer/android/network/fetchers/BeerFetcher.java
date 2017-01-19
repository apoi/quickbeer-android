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
package quickbeer.android.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.BeerMetadata;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.NetworkUtils;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static io.reark.reark.utils.Preconditions.get;

public class BeerFetcher extends FetcherBase<Uri> {
    private static final String TAG = BeerFetcher.class.getSimpleName();

    @NonNull
    private final NetworkApi networkApi;

    @NonNull
    private final NetworkUtils networkUtils;

    @NonNull
    private final BeerStore beerStore;

    @NonNull
    private final BeerMetadataStore metadataStore;

    public BeerFetcher(@NonNull final NetworkApi networkApi,
                       @NonNull final NetworkUtils networkUtils,
                       @NonNull final Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                       @NonNull final BeerStore beerStore,
                       @NonNull final BeerMetadataStore metadataStore) {
        super(updateNetworkRequestStatus);

        this.networkApi = get(networkApi);
        this.networkUtils = get(networkUtils);
        this.beerStore = get(beerStore);
        this.metadataStore = get(metadataStore);
    }

    @Override
    public void fetch(@NonNull final Intent intent) {
        final int beerId = get(intent).getIntExtra("id", -1);

        if (beerId != -1) {
            fetchBeer(beerId);
        } else {
            Log.e(TAG, "No id provided in the intent extras");
        }
    }

    private void fetchBeer(final int beerId) {
        Log.d(TAG, "fetchBeer(" + beerId + ")");

        if (isOngoingRequest(beerId)) {
            Log.d(TAG, "Found an ongoing request for beer " + beerId);
            return;
        }

        final String uri = getUniqueUri(beerId);

        Subscription subscription = createNetworkObservable(beerId)
                .subscribeOn(Schedulers.computation())
                .doOnSubscribe(() -> startRequest(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .doOnNext(beerStore::put)
                .map(BeerMetadata::newUpdate)
                .subscribe(metadataStore::put,
                           Log.onError(TAG, "Error fetching beer " + beerId));

        addRequest(beerId, subscription);
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

    @NonNull
    public static String getUniqueUri(final int id) {
        return Beer.class + "/" + id;
    }
}
