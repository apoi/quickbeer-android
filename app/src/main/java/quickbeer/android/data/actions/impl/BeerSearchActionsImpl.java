/*
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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

package quickbeer.android.data.actions.impl;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.pojo.NetworkRequestStatus;
import polanski.option.Option;
import quickbeer.android.Constants;
import quickbeer.android.data.actions.BeerSearchActions;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.network.NetworkService;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.fetchers.BeerSearchFetcher;
import quickbeer.android.rx.RxUtils;
import quickbeer.android.utils.StringUtils;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;
import timber.log.Timber;

public class BeerSearchActionsImpl extends ApplicationDataLayer implements BeerSearchActions {

    @NonNull
    private final NetworkRequestStatusStore requestStatusStore;

    @NonNull
    private final BeerListStore beerListStore;

    @Inject
    public BeerSearchActionsImpl(@NonNull Context context,
                                 @NonNull NetworkRequestStatusStore requestStatusStore,
                                 @NonNull BeerListStore beerListStore) {
        super(context);

        this.requestStatusStore = requestStatusStore;
        this.beerListStore = beerListStore;
    }

    //// SEARCH BEERS

    @Override
    @NonNull
    public Observable<List<String>> searchQueries() {
        Timber.v("searchQueries");

        return beerListStore.getOnce()
                .toObservable()
                .flatMap(Observable::from)
                .map(ItemList::getKey)
                .filter(search -> !search.startsWith(Constants.META_QUERY_PREFIX))
                .toList();
    }

    @NotNull
    @Override
    public Observable<DataStreamNotification<ItemList<String>>> search(@NotNull String query) {
        Timber.v("search");

        return triggerSearch(query, list -> list.getItems().isEmpty());
    }

    @NotNull
    @Override
    public Single<Boolean> fetchSearch(@NotNull String query) {
        Timber.v("fetchSearch");

        return triggerSearch(query, list -> true)
                .filter(DataStreamNotification::isCompleted)
                .map(DataStreamNotification::isCompletedWithSuccess)
                .first()
                .toSingle();
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> triggerSearch(@NonNull String query, @NonNull Func1<ItemList<String>, Boolean> needsReload) {
        Timber.v("triggerSearch(%s)", query);

        String normalized = StringUtils.normalize(query);

        // Trigger a fetch only if there was no cached result
        Observable<Option<ItemList<String>>> triggerFetchIfEmpty =
                beerListStore.getOnce(BeerSearchFetcher.getQueryId(RateBeerService.SEARCH, normalized))
                        .toObservable()
                        .filter(option -> option.match(needsReload::call, () -> true))
                        .doOnNext(__ -> {
                            Timber.v("Search not cached, fetching");
                            fetchBeerSearch(normalized);
                        });

        return getBeerSearchResultStream(normalized)
                .mergeWith(triggerFetchIfEmpty.flatMap(__ -> Observable.empty()));
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> getBeerSearchResultStream(@NonNull String query) {
        Timber.v("getBeerSearchResultStream(%s)", query);

        String queryId = BeerSearchFetcher.getQueryId(RateBeerService.SEARCH, query);
        String uri = BeerSearchFetcher.getUniqueUri(queryId);

        Observable<NetworkRequestStatus> requestStatusObservable =
                requestStatusStore.getOnceAndStream(NetworkRequestStatusStore.Companion.requestIdForUri(uri))
                        .compose(RxUtils::pickValue); // No need to filter stale statuses?

        Observable<ItemList<String>> beerSearchObservable =
                beerListStore.getOnceAndStream(queryId)
                        .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                requestStatusObservable, beerSearchObservable);
    }

    private int fetchBeerSearch(@NonNull String query) {
        Timber.v("fetchBeerSearch(%s)", query);

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.SEARCH.toString());
        intent.putExtra("listenerId", listenerId);
        intent.putExtra("searchString", query);
        getContext().startService(intent);

        return listenerId;
    }
}
