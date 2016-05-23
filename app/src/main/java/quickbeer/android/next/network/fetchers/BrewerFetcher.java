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
import quickbeer.android.next.data.store.BrewerStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.utils.NetworkUtils;
import quickbeer.android.next.pojo.Brewer;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class BrewerFetcher extends FetcherBase {
    private static final String TAG = BrewerFetcher.class.getSimpleName();

    private final NetworkApi networkApi;
    private final NetworkUtils networkUtils;
    private final BrewerStore brewerStore;

    public BrewerFetcher(@NonNull NetworkApi networkApi,
                         @NonNull NetworkUtils networkUtils,
                         @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                         @NonNull BrewerStore brewerStore) {
        super(updateNetworkRequestStatus);

        Preconditions.checkNotNull(networkApi, "Network API cannot be null.");
        Preconditions.checkNotNull(networkUtils, "Network utils cannot be null.");
        Preconditions.checkNotNull(brewerStore, "Brewer store cannot be null.");

        this.networkApi = networkApi;
        this.networkUtils = networkUtils;
        this.brewerStore = brewerStore;
    }

    @Override
    public void fetch(Intent intent) {
        Preconditions.checkNotNull(intent, "Fetch intent cannot be null.");

        final int brewerId = intent.getIntExtra("id", -1);
        if (brewerId != -1) {
            fetchBrewer(brewerId);
        } else {
            Log.e(TAG, "No id provided in the intent extras");
        }
    }

    private void fetchBrewer(final int brewerId) {
        Log.d(TAG, "fetchBrewer(" + brewerId + ")");

        if (requestMap.containsKey(brewerId) && !requestMap.get(brewerId).isUnsubscribed()) {
            Log.d(TAG, "Found an ongoing request for brewer " + brewerId);
            return;
        }

        final String uri = brewerStore.getUriForId(brewerId).toString();
        Subscription subscription = createNetworkObservable(brewerId)
                .subscribeOn(Schedulers.io())
                .doOnError(doOnError(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .subscribe(brewerStore::put,
                        e -> Log.e(TAG, "Error fetching brewer " + brewerId, e));

        requestMap.put(brewerId, subscription);
        startRequest(uri);
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
}
