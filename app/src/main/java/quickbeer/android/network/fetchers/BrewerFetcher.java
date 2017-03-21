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
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.BrewerMetadata;
import quickbeer.android.data.stores.BrewerMetadataStore;
import quickbeer.android.data.stores.BrewerStore;
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

public class BrewerFetcher extends FetcherBase<Uri> {

    @NonNull
    private final NetworkApi networkApi;

    @NonNull
    private final NetworkUtils networkUtils;

    @NonNull
    private final BrewerStore brewerStore;

    @NonNull
    private final BrewerMetadataStore metadataStore;

    public BrewerFetcher(@NonNull NetworkApi networkApi,
                         @NonNull NetworkUtils networkUtils,
                         @NonNull Action1<NetworkRequestStatus> networkRequestStatus,
                         @NonNull BrewerStore brewerStore,
                         @NonNull BrewerMetadataStore metadataStore) {
        super(networkRequestStatus);

        this.networkApi = get(networkApi);
        this.networkUtils = get(networkUtils);
        this.brewerStore = get(brewerStore);
        this.metadataStore = get(metadataStore);
    }

    @Override
    public void fetch(@NonNull Intent intent) {
        checkNotNull(intent);

        if (!intent.hasExtra("id") || !intent.hasExtra("listenerId")) {
            Timber.e("Missing required fetch parameters!");
            return;
        }

        int brewerId = get(intent).getIntExtra("id", 0);
        int listenerId = intent.getIntExtra("listenerId", 0);
        String uri = getUniqueUri(brewerId);

        if (isOngoingRequest(brewerId)) {
            Timber.d("Found an ongoing request for brewer " + brewerId);
            addListener(brewerId, listenerId);
            return;
        }

        Timber.d("fetchBrewer(" + brewerId + ")");

        Subscription subscription = createNetworkObservable(brewerId)
                .subscribeOn(Schedulers.computation())
                .flatMap(brewerStore::put)
                .doOnSubscribe(() -> startRequest(brewerId, listenerId, uri))
                .doOnSuccess(updated -> completeRequest(brewerId, uri, updated))
                .doOnError(doOnError(brewerId, uri))
                .subscribe(__ -> metadataStore.put(BrewerMetadata.newUpdate(brewerId)),
                        error -> Timber.e(error, "Error fetching brewer " + brewerId));

        addRequest(brewerId, listenerId, subscription);
    }

    @NonNull
    private Single<Brewer> createNetworkObservable(int brewerId) {
        return networkApi.getBrewer(networkUtils.createRequestParams("b", String.valueOf(brewerId)));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.BREWER;
    }

    @NonNull
    public static String getUniqueUri(final int id) {
        return Brewer.class + "/" + id;
    }
}
