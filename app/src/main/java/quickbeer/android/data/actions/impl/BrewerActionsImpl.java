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
import quickbeer.android.data.actions.BrewerActions;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.BrewerMetadata;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.data.stores.BrewerMetadataStore;
import quickbeer.android.data.stores.BrewerStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.network.NetworkService;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.fetchers.BeerSearchFetcher;
import quickbeer.android.network.fetchers.BrewerFetcher;
import quickbeer.android.rx.RxUtils;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;
import timber.log.Timber;

import static quickbeer.android.data.stores.NetworkRequestStatusStore.requestIdForUri;

public class BrewerActionsImpl extends ApplicationDataLayer implements BrewerActions {

    @NonNull
    private final NetworkRequestStatusStore requestStatusStore;

    @NonNull
    private final BeerListStore beerListStore;

    @NonNull
    private final BeerMetadataStore beerMetadataStore;

    @NonNull
    private final BrewerStore brewerStore;

    @NonNull
    private final BrewerMetadataStore brewerMetadataStore;

    @Inject
    public BrewerActionsImpl(@NonNull Context context,
                             @NonNull NetworkRequestStatusStore requestStatusStore,
                             @NonNull BeerListStore beerListStore,
                             @NonNull BeerMetadataStore beerMetadataStore,
                             @NonNull BrewerStore brewerStore,
                             @NonNull BrewerMetadataStore brewerMetadataStore) {
        super(context);

        this.requestStatusStore = requestStatusStore;
        this.beerListStore = beerListStore;
        this.beerMetadataStore = beerMetadataStore;
        this.brewerStore = brewerStore;
        this.brewerMetadataStore = brewerMetadataStore;
    }

    //// GET BREWER DETAILS

    @Override
    @NonNull
    public Observable<DataStreamNotification<Brewer>> get(int brewerId) {
        return getBrewer(brewerId, brewer -> !brewer.hasDetails());
    }

    @Override
    @NonNull
    public Single<Boolean> fetch(int brewerId) {
        return getBrewer(brewerId, brewer -> true)
                .filter(DataStreamNotification::isCompleted)
                .map(DataStreamNotification::isCompletedWithSuccess)
                .first()
                .toSingle();
    }

    @NonNull
    public Observable<DataStreamNotification<Brewer>> getBrewer(int brewerId, @NonNull Func1<Brewer, Boolean> needsReload) {
        Timber.v("getBrewer(%s)", brewerId);

        // Trigger a fetch only if full details haven't been fetched
        Observable<Option<Brewer>> triggerFetchIfEmpty =
                brewerStore.getOnce(brewerId)
                        .toObservable()
                        .filter(option -> option.match(needsReload::call, () -> true))
                        .doOnNext(__ -> {
                            Timber.v("Fetching brewer data");
                            fetchBrewer(brewerId);
                        });

        return getBrewerResultStream(brewerId)
                .mergeWith(triggerFetchIfEmpty.flatMap(__ -> Observable.empty()))
                .distinctUntilChanged();
    }

    @NonNull
    private Observable<DataStreamNotification<Brewer>> getBrewerResultStream(int brewerId) {
        Timber.v("getBrewerResultStream(%s)", brewerId);

        String uri = BrewerFetcher.getUniqueUri(brewerId);

        Observable<NetworkRequestStatus> requestStatusObservable =
                requestStatusStore.getOnceAndStream(requestIdForUri(uri))
                        .compose(RxUtils::pickValue);

        Observable<Brewer> brewerObservable =
                brewerStore.getOnceAndStream(brewerId)
                        .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                requestStatusObservable, brewerObservable);
    }

    private int fetchBrewer(int brewerId) {
        Timber.v("fetchBrewer(%s)", brewerId);

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.BREWER.toString());
        intent.putExtra("listenerId", listenerId);
        intent.putExtra("id", brewerId);
        getContext().startService(intent);

        return listenerId;
    }

    //// BREWER'S BEERS

    @Override
    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> beers(int brewerId) {
        Timber.v("getBrewerBeers(%s)", brewerId);

        // Trigger a fetch only if there was no cached result
        Observable<Option<ItemList<String>>> triggerFetchIfEmpty =
                beerListStore.getOnce(BeerSearchFetcher.getQueryId(RateBeerService.BREWER_BEERS, String.valueOf(brewerId)))
                        .toObservable()
                        .filter(RxUtils::isNoneOrEmpty)
                        .doOnNext(__ -> {
                            Timber.v("Search not cached, fetching");
                            fetchBrewerBeers(brewerId);
                        });

        return getBrewerBeersResultStream(brewerId)
                .mergeWith(triggerFetchIfEmpty.flatMap(__ -> Observable.empty()));
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> getBrewerBeersResultStream(int brewerId) {
        Timber.v("getBrewerBeersResultStream(%s)", brewerId);

        String queryId = BeerSearchFetcher.getQueryId(RateBeerService.BREWER_BEERS, String.valueOf(brewerId));
        String uri = BeerSearchFetcher.getUniqueUri(queryId);

        Observable<NetworkRequestStatus> requestStatusObservable =
                requestStatusStore.getOnceAndStream(requestIdForUri(uri))
                        .compose(RxUtils::pickValue);

        Observable<ItemList<String>> beerSearchObservable =
                beerListStore.getOnceAndStream(queryId)
                        .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                requestStatusObservable, beerSearchObservable);
    }

    private int fetchBrewerBeers(int brewerId) {
        Timber.v("fetchBrewerBeers(%s)", brewerId);

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.BREWER_BEERS.toString());
        intent.putExtra("listenerId", listenerId);
        intent.putExtra("brewerId", brewerId);
        getContext().startService(intent);

        return listenerId;
    }

    //// ACCESS BREWER

    @Override
    public void access(int brewerId) {
        Timber.v("accessBrewer(%s)", brewerId);

        brewerMetadataStore.put(BrewerMetadata.newAccess(brewerId));
    }
}
