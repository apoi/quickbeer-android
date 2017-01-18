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
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.BrewerMetadata;
import quickbeer.android.data.stores.BrewerMetadataStore;
import quickbeer.android.data.stores.BrewerStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.NetworkUtils;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static io.reark.reark.utils.Preconditions.get;

public class BrewerFetcher extends FetcherBase<Uri> {
    private static final String TAG = BrewerFetcher.class.getSimpleName();

    @NonNull
    private final NetworkApi networkApi;

    @NonNull
    private final NetworkUtils networkUtils;

    @NonNull
    private final BrewerStore brewerStore;

    @NonNull
    private final BrewerMetadataStore metadataStore;

    public BrewerFetcher(@NonNull final NetworkApi networkApi,
                         @NonNull final NetworkUtils networkUtils,
                         @NonNull final Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                         @NonNull final BrewerStore brewerStore,
                         @NonNull final BrewerMetadataStore metadataStore) {
        super(updateNetworkRequestStatus);

        this.networkApi = get(networkApi);
        this.networkUtils = get(networkUtils);
        this.brewerStore = get(brewerStore);
        this.metadataStore = get(metadataStore);
    }

    @Override
    public void fetch(@NonNull final Intent intent) {
        final int brewerId = get(intent).getIntExtra("id", -1);

        if (brewerId != -1) {
            fetchBrewer(brewerId);
        } else {
            Log.e(TAG, "No id provided in the intent extras");
        }
    }

    private void fetchBrewer(final int brewerId) {
        Log.d(TAG, "fetchBrewer(" + brewerId + ")");

        if (isOngoingRequest(brewerId)) {
            Log.d(TAG, "Found an ongoing request for brewer " + brewerId);
            return;
        }

        final String uri = getUniqueUri(brewerId);

        Subscription subscription = createNetworkObservable(brewerId)
                .subscribeOn(Schedulers.computation())
                .doOnSubscribe(() -> startRequest(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .doOnNext(brewerStore::put)
                .map(BrewerMetadata::newUpdate)
                .subscribe(metadataStore::put,
                        Log.onError(TAG, "Error fetching brewer " + brewerId));

        addRequest(brewerId, subscription);
    }

    @NonNull
    private Observable<Brewer> createNetworkObservable(int brewerId) {
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
