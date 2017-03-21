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
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.BeerMetadata;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.NetworkUtils;
import rx.Single;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BeerFetcher extends FetcherBase<Uri> {

    @NonNull
    private final NetworkApi networkApi;

    @NonNull
    private final NetworkUtils networkUtils;

    @NonNull
    private final BeerStore beerStore;

    @NonNull
    private final BeerMetadataStore metadataStore;

    public BeerFetcher(@NonNull NetworkApi networkApi,
                       @NonNull NetworkUtils networkUtils,
                       @NonNull Action1<NetworkRequestStatus> networkRequestStatus,
                       @NonNull BeerStore beerStore,
                       @NonNull BeerMetadataStore metadataStore) {
        super(networkRequestStatus);

        this.networkApi = get(networkApi);
        this.networkUtils = get(networkUtils);
        this.beerStore = get(beerStore);
        this.metadataStore = get(metadataStore);
    }

    @Override
    public void fetch(@NonNull Intent intent, int listenerId) {
        checkNotNull(intent);

        if (!intent.hasExtra("id")) {
            Timber.e("Missing required fetch parameters!");
            return;
        }

        int beerId = get(intent).getIntExtra("id", 0);
        String uri = getUniqueUri(beerId);

        if (isOngoingRequest(beerId)) {
            Timber.d("Found an ongoing request for beer " + beerId);
            addListener(beerId, listenerId);
            return;
        }

        Timber.d("fetchBeer(" + beerId + ")");

        Subscription subscription = createNetworkObservable(beerId)
                .subscribeOn(Schedulers.computation())
                .flatMap(beerStore::put)
                .doOnSubscribe(() -> startRequest(beerId, listenerId, uri))
                .doOnSuccess(updated -> completeRequest(listenerId, uri, updated))
                .doOnError(doOnError(beerId, uri))
                .subscribe(__ -> metadataStore.put(BeerMetadata.newUpdate(beerId)),
                        error -> Timber.e(error, "Error fetching beer " + beerId));

        addRequest(beerId, listenerId, subscription);
    }

    @NonNull
    private Single<Beer> createNetworkObservable(int beerId) {
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
