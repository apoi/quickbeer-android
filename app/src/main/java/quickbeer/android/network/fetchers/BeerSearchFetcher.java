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

import org.threeten.bp.ZonedDateTime;

import java.util.Collections;
import java.util.List;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.NetworkUtils;
import quickbeer.android.rx.RxUtils;
import quickbeer.android.utils.StringUtils;
import rx.Observable;
import rx.Single;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BeerSearchFetcher extends FetcherBase<Uri> {

    @NonNull
    protected final NetworkApi networkApi;

    @NonNull
    protected final NetworkUtils networkUtils;

    @NonNull
    private final BeerStore beerStore;

    @NonNull
    private final BeerListStore beerListStore;

    public BeerSearchFetcher(@NonNull NetworkApi networkApi,
                             @NonNull NetworkUtils networkUtils,
                             @NonNull Action1<NetworkRequestStatus> networkRequestStatus,
                             @NonNull BeerStore beerStore,
                             @NonNull BeerListStore beerListStore) {
        super(networkRequestStatus);

        this.networkApi = get(networkApi);
        this.networkUtils = get(networkUtils);
        this.beerStore = get(beerStore);
        this.beerListStore = get(beerListStore);
    }

    @Override
    public void fetch(@NonNull Intent intent, int listenerId) {
        checkNotNull(intent);

        if (!intent.hasExtra("searchString")) {
            Timber.e("Missing required fetch parameters!");
            return;
        }

        String searchString = get(intent).getStringExtra("searchString");
        fetchBeerSearch(StringUtils.normalize(searchString), listenerId);
    }

    protected void fetchBeerSearch(@NonNull String query, int listenerId) {
        Timber.d("fetchBeerSearch(" + query + ")");

        final String queryId = getQueryId(getServiceUri(), get(query));
        final String uri = getUniqueUri(queryId);
        int requestId = uri.hashCode();

        if (isOngoingRequest(requestId)) {
            Timber.d("Found an ongoing request for search " + queryId);
            addListener(requestId, listenerId);
            return;
        }

        Subscription subscription = createNetworkObservable(query)
                .subscribeOn(Schedulers.computation())
                .toObservable()
                .map(this::sort)
                .flatMap(Observable::from)
                .doOnNext(beerStore::put)
                .map(Beer::id)
                .toList()
                .toSingle()
                .map(beerIds -> ItemList.create(queryId, beerIds, ZonedDateTime.now()))
                .flatMap(beerListStore::put)
                .doOnSubscribe(() -> startRequest(requestId, listenerId, uri))
                .doOnSuccess(updated -> completeRequest(requestId, uri, updated))
                .doOnError(doOnError(requestId, uri))
                .subscribe(RxUtils::nothing,
                        error -> Timber.e(error, "Error fetching beer search for %s", uri));

        addRequest(requestId, listenerId, subscription);
    }

    @SuppressWarnings({"CallToStringCompareTo", "IfMayBeConditional"})
    @NonNull
    protected List<Beer> sort(@NonNull List<Beer> list) {
        Collections.sort(list, (first, second) -> {
            String firstName = first.name();
            String secondName = second.name();

            if (firstName == null) {
                if (secondName == null) {
                    return first.id().compareTo(second.id());
                } else {
                    return -1;
                }
            } else {
                if (secondName == null) {
                    return 1;
                } else {
                    return firstName.compareTo(secondName);
                }
            }
        });

        return list;
    }

    @NonNull
    protected Single<List<Beer>> createNetworkObservable(@NonNull String searchString) {
        return networkApi.search(networkUtils.createRequestParams("bn", get(searchString)));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.SEARCH;
    }

    // Brewer search store needs separate query identifiers for normal searches and fixed searches
    // (top50, top in country, top in style). Fixed searches come attached with a service uri
    // identifier to make sure they stand apart from the normal searches.
    public static String getQueryId(@NonNull Uri serviceUri, @NonNull String query) {
        checkNotNull(serviceUri);
        checkNotNull(query);

        if (serviceUri.equals(RateBeerService.SEARCH)) {
            return query;
        } else if (!query.isEmpty()) {
            return String.format("%s_%s", serviceUri, query);
        } else {
            return serviceUri.toString();
        }
    }

    public static String getQueryId(@NonNull Uri serviceUri) {
        return getQueryId(serviceUri, "");
    }

    @NonNull
    public static String getUniqueUri(@NonNull String id) {
        return ItemList.class + "/beerSearch/" + id;
    }
}
