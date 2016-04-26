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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.store.BeerListStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.utils.NetworkUtils;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.SearchList;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public class BeerSearchFetcher extends FetcherBase {
    private static final String TAG = BeerSearchFetcher.class.getSimpleName();

    protected final NetworkApi networkApi;
    protected final NetworkUtils networkUtils;

    private final BeerStore beerStore;
    private final BeerListStore beerListStore;

    public BeerSearchFetcher(@NonNull NetworkApi networkApi,
                             @NonNull NetworkUtils networkUtils,
                             @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                             @NonNull BeerStore beerStore,
                             @NonNull BeerListStore beerListStore) {
        super(updateNetworkRequestStatus);

        Preconditions.checkNotNull(networkApi, "Network api cannot be null.");
        Preconditions.checkNotNull(networkUtils, "Network utils cannot be null.");
        Preconditions.checkNotNull(beerStore, "Beer store cannot be null.");
        Preconditions.checkNotNull(beerListStore, "Beer search store cannot be null.");

        this.networkApi = networkApi;
        this.networkUtils = networkUtils;
        this.beerStore = beerStore;
        this.beerListStore = beerListStore;
    }

    @Override
    public void fetch(@NonNull Intent intent) {
        final String searchString = intent.getStringExtra("searchString");
        if (searchString != null) {
            fetchBeerSearch(searchString);
        } else {
            Log.e(TAG, "No searchString provided in the intent extras");
        }
    }

    protected void fetchBeerSearch(@NonNull final String query) {
        Preconditions.checkNotNull(query, "Search string cannot be null.");

        String queryId = beerListStore.getQueryId(getServiceUri(), query);
        Log.d(TAG, "fetchBeerSearch(" + queryId + ")");

        if (requestMap.containsKey(queryId.hashCode()) &&
                !requestMap.get(queryId.hashCode()).isUnsubscribed()) {
            Log.d(TAG, "Found an ongoing request for search " + queryId);
            return;
        }

        final String uri = beerListStore.getUriForId(queryId).toString();
        Subscription subscription = createNetworkObservable(query)
                .map((beers) -> {
                    final List<Integer> beerIds = new ArrayList<>();
                    for (Beer beer : beers) {
                        beerStore.put(beer);
                        beerIds.add(beer.getId());
                    }
                    return new SearchList<String>(queryId, beerIds, new Date());
                })
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .subscribe(beerListStore::put,
                        e -> Log.e(TAG, "Error fetching beer search for '" + queryId + "'", e));

        requestMap.put(queryId.hashCode(), subscription);
        startRequest(uri);
    }

    @NonNull
    protected Observable<List<Beer>> createNetworkObservable(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string cannot be null.");

        return networkApi.search(networkUtils.createRequestParams("bn", searchString));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.SEARCH;
    }
}
