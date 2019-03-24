/*
 * This file is part of QuickBeer.
 * Copyright (C) 2019 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.network.fetchers.impl;

import android.content.Intent;
import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import io.reark.reark.pojo.NetworkRequestStatus;
import org.jetbrains.annotations.NotNull;
import org.threeten.bp.ZonedDateTime;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.fetchers.CheckingFetcher;
import quickbeer.android.network.utils.NetworkUtils;
import timber.log.Timber;

import java.util.Collections;
import java.util.List;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

@SuppressWarnings({"IfMayBeConditional", "CallToStringCompareTo"})
public class BeerSearchFetcher extends CheckingFetcher {

    public static final String NAME = "__search";
    public static final String SEARCH = "searchString";

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
                             @NonNull Consumer<NetworkRequestStatus> networkRequestStatus,
                             @NonNull BeerStore beerStore,
                             @NonNull BeerListStore beerListStore) {
        this(networkApi, networkUtils, networkRequestStatus, beerStore, beerListStore, NAME);
    }

    public BeerSearchFetcher(@NonNull NetworkApi networkApi,
                             @NonNull NetworkUtils networkUtils,
                             @NonNull Consumer<NetworkRequestStatus> networkRequestStatus,
                             @NonNull BeerStore beerStore,
                             @NonNull BeerListStore beerListStore,
                             @NonNull String name) {
        super(networkRequestStatus, name);

        this.networkApi = get(networkApi);
        this.networkUtils = get(networkUtils);
        this.beerStore = get(beerStore);
        this.beerListStore = get(beerListStore);
    }

    @Override
    public @NotNull List<String> required() {
        return Collections.singletonList(SEARCH);
    }

    @Override
    public void fetch(@NonNull Intent intent, int listenerId) {
        if (!validateParams(intent)) return;

        String searchString = get(intent).getStringExtra(SEARCH);
        fetchBeerSearch(searchString, listenerId);
    }

    protected void fetchBeerSearch(@NonNull String query, int listenerId) {
        Timber.d("fetchBeerSearch(" + query + ")");

        final String queryId = getQueryId(getServiceUri(), get(query));
        final String uri = getUniqueUri(queryId);
        int requestId = uri.hashCode();

        addListener(requestId, listenerId);

        if (isOngoingRequest(requestId)) {
            Timber.d("Found an ongoing request for search " + queryId);
            return;
        }

        Disposable disposable = createNetworkObservable(query)
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMap(Observable::fromIterable)
                .flatMap(beer -> beerStore.put(beer).toObservable().map(__ -> beer))
                .toList()
                .map(this::sort)
                .flatMapObservable(Observable::fromIterable)
                .map(Beer::getId)
                .toList()
                .map(beerIds -> ItemList.Companion.create(queryId, beerIds, ZonedDateTime.now()))
                .flatMap(beerListStore::put)
                .doOnSubscribe(__ -> startRequest(requestId, uri))
                .doOnSuccess(updated -> completeRequest(requestId, uri, updated))
                .doOnError(doOnError(requestId, uri))
                .subscribe(Functions.emptyConsumer(),
                        error -> Timber.w(error, "Error fetching beer search for %s", uri));

        addRequest(requestId, disposable);
    }

    @SuppressWarnings({"CallToStringCompareTo", "IfMayBeConditional"})
    @NonNull
    protected List<Beer> sort(@NonNull List<Beer> list) {
        return sortByName(list);
    }

    @NonNull
    protected static List<Beer> sortByName(@NonNull List<Beer> list) {
        Collections.sort(list, (first, second) -> {
            String firstName = first.getName();
            String secondName = second.getName();

            if (firstName == null) {
                if (secondName == null) {
                    return Integer.valueOf(first.getId()).compareTo(second.getId());
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
    protected static List<Beer> sortByTickDate(@NonNull List<Beer> list) {
        Collections.sort(list, (first, second) -> {
            ZonedDateTime firstDate = first.getTickDate();
            ZonedDateTime secondDate = second.getTickDate();

            if (firstDate == null) {
                if (secondDate == null) {
                    return Integer.valueOf(first.getId()).compareTo(second.getId());
                } else {
                    return -1;
                }
            } else {
                if (secondDate == null) {
                    return 1;
                } else {
                    return secondDate.compareTo(firstDate);
                }
            }
        });

        return list;
    }

    @NonNull
    protected static List<Beer> sortByRating(@NonNull List<Beer> list) {
        Collections.sort(list, (first, second) -> {
            Float firstRating = first.getAverageRating();
            Float secondRating = second.getAverageRating();

            if (firstRating == null) {
                if (secondRating == null) {
                    return Integer.valueOf(first.getId()).compareTo(second.getId());
                } else {
                    return -1;
                }
            } else {
                if (secondRating == null) {
                    return 1;
                } else {
                    return secondRating.compareTo(firstRating);
                }
            }
        });

        return list;
    }

    @NonNull
    protected Single<List<Beer>> createNetworkObservable(@NonNull String searchString) {
        return networkApi.search(networkUtils.createRequestParams("bn", get(searchString)));
    }

    // Beer search store needs separate query identifiers for normal searches and fixed searches
    // (top50, top in country, top in style). Fixed searches come attached with a service uri
    // identifier to make sure they stand apart from the normal searches.
    public static String getQueryId(@NonNull String serviceUri, @NonNull String query) {
        checkNotNull(serviceUri);
        checkNotNull(query);

        if (serviceUri == NAME) {
            return query;
        } else if (!query.isEmpty()) {
            return String.format("%s_%s", serviceUri, query);
        } else {
            return serviceUri;
        }
    }

    public static String getQueryId(@NonNull String serviceUri) {
        return getQueryId(serviceUri, "");
    }

    @NonNull
    public static String getUniqueUri(@NonNull String id) {
        return ItemList.class + "/beerSearch/" + id;
    }
}
