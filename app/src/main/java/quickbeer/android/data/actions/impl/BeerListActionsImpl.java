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

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.pojo.NetworkRequestStatus;
import polanski.option.Option;
import quickbeer.android.data.actions.BeerListActions;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.network.NetworkService;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.fetchers.BeerSearchFetcher;
import quickbeer.android.rx.RxUtils;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;
import timber.log.Timber;

public class BeerListActionsImpl extends ApplicationDataLayer implements BeerListActions {

    @NonNull
    private final NetworkRequestStatusStore requestStatusStore;

    @NonNull
    private final BeerListStore beerListStore;

    @NonNull
    private final BeerMetadataStore beerMetadataStore;

    @Inject
    public BeerListActionsImpl(@NonNull Context context,
                               @NonNull NetworkRequestStatusStore requestStatusStore,
                               @NonNull BeerListStore beerListStore,
                               @NonNull BeerMetadataStore beerMetadataStore) {
        super(context);

        this.requestStatusStore = requestStatusStore;
        this.beerListStore = beerListStore;
        this.beerMetadataStore = beerMetadataStore;
    }

    //// ACCESSED BEERS

    @Override
    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> accessed() {
        Timber.v("accessed");

        return beerMetadataStore.getAccessedIdsOnce()
                .map(ids -> new ItemList<String>(null, ids, null))
                .map(DataStreamNotification::onNext);
    }

    //// TOP BEERS

    @Override
    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> topBeers() {
        Timber.v("topBeers");

        return triggerGet(list -> list.getItems().isEmpty());
    }

    @Override
    @NonNull
    public Single<Boolean> fetchTopBeers() {
        Timber.v("fetchTopBeers");

        return triggerGet(list -> true)
                .filter(DataStreamNotification::isCompleted)
                .map(DataStreamNotification::isCompletedWithSuccess)
                .first()
                .toSingle();
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> triggerGet(@NonNull Func1<ItemList<String>, Boolean> needsReload) {
        Timber.v("triggerGetBeers");

        // Trigger a fetch only if there was no cached result
        Observable<Option<ItemList<String>>> triggerFetchIfEmpty =
                beerListStore.getOnce(BeerSearchFetcher.getQueryId(RateBeerService.TOP50))
                        .toObservable()
                        .filter(option -> option.match(needsReload::call, () -> true))
                        .doOnNext(__ -> {
                            Timber.v("Search not cached, fetching");
                            triggerFetch();
                        });

        return getTopBeersResultStream()
                .mergeWith(triggerFetchIfEmpty.flatMap(__ -> Observable.empty()));
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> getTopBeersResultStream() {
        Timber.v("getTopBeersResultStream");

        String queryId = BeerSearchFetcher.getQueryId(RateBeerService.TOP50);
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

    private int triggerFetch() {
        Timber.v("triggerFetch");

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.TOP50.toString());
        intent.putExtra("listenerId", listenerId);
        getContext().startService(intent);

        return listenerId;
    }
}
