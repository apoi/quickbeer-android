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
import quickbeer.android.data.actions.BarcodeActions;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.network.NetworkService;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.fetchers.BeerSearchFetcher;
import quickbeer.android.rx.RxUtils;
import rx.Observable;
import rx.Single;
import timber.log.Timber;

import static quickbeer.android.data.stores.NetworkRequestStatusStore.requestIdForUri;

public class BarcodeActionsImpl extends ApplicationDataLayer implements BarcodeActions {

    @NonNull
    private final NetworkRequestStatusStore requestStatusStore;

    @NonNull
    private final BeerListStore beerListStore;

    @Inject
    public BarcodeActionsImpl(@NonNull Context context,
                              @NonNull NetworkRequestStatusStore requestStatusStore,
                              @NonNull BeerListStore beerListStore) {
        super(context);

        this.requestStatusStore = requestStatusStore;
        this.beerListStore = beerListStore;
    }

    //// BARCODE SEARCH

    @Override
    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> get(@NonNull String barcode) {
        Timber.v("getBarcodeSearch(%s)", barcode);

        // Trigger a fetch only if there was no cached result
        Observable<Option<ItemList<String>>> triggerFetchIfEmpty =
                beerListStore.getOnce(BeerSearchFetcher.getQueryId(RateBeerService.BARCODE, barcode))
                        .toObservable()
                        .filter(RxUtils::isNoneOrEmpty)
                        .doOnNext(__ -> {
                            Timber.v("Search not cached, fetching");
                            fetchBarcodeSearch(barcode);
                        });

        return getBarcodeSearchResultStream(barcode)
                .mergeWith(triggerFetchIfEmpty.flatMap(__ -> Observable.empty()));
    }

    @NotNull
    @Override
    public Single<Boolean> fetch(@NotNull String barcode) {
        return Single.just(false);
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> getBarcodeSearchResultStream(@NonNull String barcode) {
        Timber.v("getBarcodeSearchResultStream(%s)", barcode);

        String queryId = BeerSearchFetcher.getQueryId(RateBeerService.BARCODE, barcode);
        String uri = BeerSearchFetcher.getUniqueUri(queryId);

        Observable<NetworkRequestStatus> requestStatusObservable =
                requestStatusStore.getOnceAndStream(requestIdForUri(uri))
                        .compose(RxUtils::pickValue); // No need to filter stale statuses?

        Observable<ItemList<String>> barcodeSearchObservable =
                beerListStore.getOnceAndStream(queryId)
                        .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                requestStatusObservable, barcodeSearchObservable);
    }

    private int fetchBarcodeSearch(@NonNull String barcode) {
        Timber.v("fetchBarcodeSearch(%s)", barcode);

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.BARCODE.toString());
        intent.putExtra("listenerId", listenerId);
        intent.putExtra("barcode", barcode);
        getContext().startService(intent);

        return listenerId;
    }
}
