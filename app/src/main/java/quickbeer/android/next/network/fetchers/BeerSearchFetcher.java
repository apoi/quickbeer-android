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
import quickbeer.android.next.data.store.BeerListStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.utils.NetworkUtils;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.ItemList;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static io.reark.reark.utils.Preconditions.get;

public class BeerSearchFetcher extends FetcherBase<Uri> {
    private static final String TAG = BeerSearchFetcher.class.getSimpleName();

    protected final NetworkApi networkApi;
    protected final NetworkUtils networkUtils;

    private final BeerStore beerStore;
    private final BeerListStore beerListStore;

    public BeerSearchFetcher(@NonNull final NetworkApi networkApi,
                             @NonNull final NetworkUtils networkUtils,
                             @NonNull final Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                             @NonNull final BeerStore beerStore,
                             @NonNull final BeerListStore beerListStore) {
        super(updateNetworkRequestStatus);

        this.networkApi = get(networkApi);
        this.networkUtils = get(networkUtils);
        this.beerStore = get(beerStore);
        this.beerListStore = get(beerListStore);
    }

    @Override
    public void fetch(@NonNull final Intent intent) {
        final String searchString = get(intent).getStringExtra("searchString");

        if (searchString != null) {
            fetchBeerSearch(searchString);
        } else {
            Log.e(TAG, "No searchString provided in the intent extras");
        }
    }

    protected void fetchBeerSearch(@NonNull final String query) {
        Log.d(TAG, "fetchBeerSearch(" + query + ")");

        final String uri = getUniqueUri(get(query));

        if (isOngoingRequest(uri.hashCode())) {
            Log.d(TAG, "Found an ongoing request for search " + query);
            return;
        }

        Subscription subscription = createNetworkObservable(query)
                .subscribeOn(Schedulers.computation())
                .map((beers) -> {
                    final List<Integer> beerIds = new ArrayList<>(10);
                    for (final Beer beer : beers) {
                        beerStore.put(beer);
                        beerIds.add(beer.getId());
                    }
                    return new ItemList<>(uri, beerIds, new Date());
                })
                .doOnSubscribe(() -> startRequest(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .subscribe(beerListStore::put,
                           e -> Log.e(TAG, "Error fetching beer search for '" + uri + "'", e));

        addRequest(uri.hashCode(), subscription);
    }

    @NonNull
    protected Observable<List<Beer>> createNetworkObservable(@NonNull final String searchString) {
        return networkApi.search(networkUtils.createRequestParams("bn", get(searchString)));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.SEARCH;
    }

    @NonNull
    public static String getUniqueUri(@NonNull final String id) {
        return ItemList.class + "/" + id;
    }
}
