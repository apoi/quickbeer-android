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

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.pojo.NetworkRequestStatus;
import polanski.option.Option;
import quickbeer.android.data.actions.StyleActions;
import quickbeer.android.data.pojos.BeerStyle;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerStyleStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.network.NetworkService;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.fetchers.BeerSearchFetcher;
import quickbeer.android.rx.RxUtils;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;
import timber.log.Timber;

import static quickbeer.android.data.stores.NetworkRequestStatusStore.requestIdForUri;

public class StyleActionsImpl extends ApplicationDataLayer implements StyleActions {

    @NonNull
    private final NetworkRequestStatusStore requestStatusStore;

    @NonNull
    private final BeerListStore beerListStore;

    @NonNull
    private final BeerStyleStore beerStyleStore;

    @Inject
    public StyleActionsImpl(@NonNull Context context,
                            @NonNull NetworkRequestStatusStore requestStatusStore,
                            @NonNull BeerListStore beerListStore,
                            @NonNull BeerStyleStore beerStyleStore) {
        super(context);

        this.requestStatusStore = requestStatusStore;
        this.beerListStore = beerListStore;
        this.beerStyleStore = beerStyleStore;
    }

    //// STYLES

    @Override
    @NonNull
    public Single<Option<BeerStyle>> get(int styleId) {
        Timber.v("get(%s)", styleId);

        return beerStyleStore.getOnce(styleId);
    }

    //// BEERS IN STYLE


    @NotNull
    @Override
    public Observable<DataStreamNotification<ItemList<String>>> beers(int styleId) {
        Timber.v("beers(%s)", styleId);

        return triggerGetBeers(styleId, list -> list.getItems().isEmpty());
    }

    @NotNull
    @Override
    public Single<Boolean> fetchBeers(int styleId) {
        Timber.v("fetchBeers(%s)", styleId);

        return triggerGetBeers(styleId, list -> true)
                .filter(DataStreamNotification::isCompleted)
                .map(DataStreamNotification::isCompletedWithSuccess)
                .first()
                .toSingle();
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> triggerGetBeers(int styleId, @NonNull Func1<ItemList<String>, Boolean> needsReload) {
        Timber.v("triggerGetBeers(%s)", styleId);

        // Trigger a fetch only if there was no cached result
        Observable<Option<ItemList<String>>> triggerFetchIfEmpty =
                beerListStore.getOnce(BeerSearchFetcher.getQueryId(RateBeerService.STYLE, String.valueOf(styleId)))
                        .toObservable()
                        .filter(option -> option.match(needsReload::call, () -> true))
                        .doOnNext(__ -> {
                            Timber.v("Search not cached, fetching");
                            fetchBeersInStyle(styleId);
                        });

        return getBeersInStyleResultStream(styleId)
                .mergeWith(triggerFetchIfEmpty.flatMap(__ -> Observable.empty()));
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> getBeersInStyleResultStream(int styleId) {
        Timber.v("getBeersInStyleResultStream(%s)", styleId);

        String queryId = BeerSearchFetcher.getQueryId(RateBeerService.STYLE, String.valueOf(styleId));
        String uri = BeerSearchFetcher.getUniqueUri(queryId);

        Observable<NetworkRequestStatus> requestStatusObservable =
                requestStatusStore.getOnceAndStream(requestIdForUri(uri))
                        .compose(RxUtils::pickValue); // No need to filter stale statuses?

        Observable<ItemList<String>> beerSearchObservable =
                beerListStore.getOnceAndStream(queryId)
                        .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                requestStatusObservable, beerSearchObservable);
    }

    private int fetchBeersInStyle(int styleId) {
        Timber.v("fetchBeersInStyle(%s)", styleId);

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.STYLE.toString());
        intent.putExtra("listenerId", listenerId);
        intent.putExtra("styleId", styleId);
        getContext().startService(intent);

        return listenerId;
    }
}
