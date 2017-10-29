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
import quickbeer.android.data.actions.BeerActions;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.BeerMetadata;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.data.stores.ReviewStore;
import quickbeer.android.network.NetworkService;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.fetchers.BeerFetcher;
import quickbeer.android.network.fetchers.ReviewFetcher;
import quickbeer.android.network.fetchers.TickBeerFetcher;
import quickbeer.android.rx.RxUtils;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;
import timber.log.Timber;

public class BeerActionsImpl extends ApplicationDataLayer implements BeerActions {

    @NonNull
    private final NetworkRequestStatusStore requestStatusStore;

    @NonNull
    private final BeerStore beerStore;

    @NonNull
    private final BeerMetadataStore beerMetadataStore;

    @NonNull
    private final ReviewStore reviewStore;

    @NonNull
    private final ReviewListStore reviewListStore;

    @Inject
    public BeerActionsImpl(@NonNull Context context,
                           @NonNull NetworkRequestStatusStore requestStatusStore,
                           @NonNull BeerStore beerStore,
                           @NonNull BeerMetadataStore beerMetadataStore,
                           @NonNull ReviewStore reviewStore,
                           @NonNull ReviewListStore reviewListStore) {
        super(context);

        this.requestStatusStore = requestStatusStore;
        this.beerStore = beerStore;
        this.beerMetadataStore = beerMetadataStore;
        this.reviewStore = reviewStore;
        this.reviewListStore = reviewListStore;
    }

    //// API

    @Override
    @NonNull
    public Observable<DataStreamNotification<Beer>> get(int beerId) {
        return getBeer(beerId, Beer::basicDataMissing);
    }

    @Override
    @NonNull
    public Observable<DataStreamNotification<Beer>> getDetails(int beerId) {
        return getBeer(beerId, Beer::detailedDataMissing);
    }

    @Override
    @NonNull
    public Single<Boolean> fetch(int beerId) {
        return getBeer(beerId, beer -> true)
                .filter(DataStreamNotification::isCompleted)
                .map(DataStreamNotification::isCompletedWithSuccess)
                .first()
                .toSingle();
    }

    @Override
    public void access(int beerId) {
        Timber.v("access(%s)", beerId);

        beerMetadataStore.put(BeerMetadata.Companion.newAccess(beerId));
    }

    @Override
    @NonNull
    public Observable<DataStreamNotification<ItemList<Integer>>> getReviews(int beerId) {
        Timber.v("getReviews(%s)", beerId);

        // Trigger a fetch only if there was no cached result
        Observable<Option<ItemList<Integer>>> triggerFetchIfEmpty =
                reviewListStore.getOnce(beerId)
                        .toObservable()
                        .filter(RxUtils::isNoneOrEmpty)
                        .doOnNext(__ -> {
                            Timber.v("Reviews not cached, fetching");
                            fetchReviews(beerId, 1);
                        });

        return getReviewsResultStream(beerId)
                .mergeWith(triggerFetchIfEmpty.flatMap(__ -> Observable.empty()));
    }

    @Override
    public void fetchReviews(int beerId, int page) {
        triggerReviewsFetch(beerId, page);
    }

    @Override
    @NonNull
    public Observable<DataStreamNotification<Void>> tick(int beerId, int rating) {
        Timber.v("tick(%s, %s)", beerId, rating);

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.TICK_BEER.toString());
        intent.putExtra("listenerId", listenerId);
        intent.putExtra("beerId", beerId);
        intent.putExtra("rating", rating);
        getContext().startService(intent);

        String uri = TickBeerFetcher.Companion.getUniqueUri(beerId, rating);

        Observable<NetworkRequestStatus> requestStatusObservable =
                requestStatusStore.getOnceAndStream(NetworkRequestStatusStore.Companion.requestIdForUri(uri))
                        .compose(RxUtils::pickValue)
                        .filter(status -> status.forListener(listenerId));

        return DataLayerUtils.createDataStreamNotificationObservable(
                requestStatusObservable, Observable.never());
    }

    //// BEER

    @NonNull
    private Observable<DataStreamNotification<Beer>> getBeer(int beerId, @NonNull Func1<Beer, Boolean> needsReload) {
        Timber.v("getBeer(%s)", beerId);

        // Trigger a fetch only if full details haven't been fetched
        Observable<Option<Beer>> triggerFetchIfEmpty =
                beerStore.getOnce(beerId)
                        .toObservable()
                        .filter(option -> option.match(needsReload::call, () -> true))
                        .doOnNext(__ -> {
                            Timber.v("Fetching beer data");
                            triggerBeerFetch(beerId);
                        });

        return getBeerResultStream(beerId)
                .mergeWith(triggerFetchIfEmpty.flatMap(__ -> Observable.empty()))
                .distinctUntilChanged();
    }

    @NonNull
    private Observable<DataStreamNotification<Beer>> getBeerResultStream(int beerId) {
        Timber.v("getBeerResultStream(%s)", beerId);

        String uri = BeerFetcher.getUniqueUri(beerId);

        Observable<NetworkRequestStatus> requestStatusObservable =
                requestStatusStore.getOnceAndStream(NetworkRequestStatusStore.Companion.requestIdForUri(uri))
                        .compose(RxUtils::pickValue); // No need to filter stale statuses

        Observable<Beer> beerObservable =
                beerStore.getOnceAndStream(beerId)
                        .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                requestStatusObservable, beerObservable);
    }

    private int triggerBeerFetch(int beerId) {
        Timber.v("triggerBeerFetch(%s)", beerId);

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.BEER.toString());
        intent.putExtra("listenerId", listenerId);
        intent.putExtra("id", beerId);
        getContext().startService(intent);

        return listenerId;
    }

    //// GET REVIEWS

    @NonNull
    private Observable<DataStreamNotification<ItemList<Integer>>> getReviews(int beerId, @NonNull Func1<ItemList<Integer>, Boolean> needsReload) {
        Timber.v("getReviews(%s)", beerId);

        // Trigger a fetch only if there was no cached result
        Observable<Option<ItemList<Integer>>> triggerFetchIfEmpty =
                reviewListStore.getOnce(beerId)
                        .toObservable()
                        .filter(option -> option.match(needsReload::call, () -> true))
                        .doOnNext(__ -> {
                            Timber.v("Reviews not cached, fetching");
                            triggerReviewsFetch(beerId, 1);
                        });

        return getReviewsResultStream(beerId)
                .mergeWith(triggerFetchIfEmpty.flatMap(__ -> Observable.empty()));
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<Integer>>> getReviewsResultStream(int beerId) {
        Timber.v("getReviewsResultStream(%s)", beerId);

        String uri = ReviewFetcher.getUniqueUri(beerId);

        Observable<NetworkRequestStatus> requestStatusObservable =
                requestStatusStore.getOnceAndStream(NetworkRequestStatusStore.Companion.requestIdForUri(uri))
                        .compose(RxUtils::pickValue); // No need to filter stale statuses?

        Observable<ItemList<Integer>> reviewListObservable =
                reviewListStore.getOnceAndStream(beerId)
                        .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                requestStatusObservable, reviewListObservable);
    }

    private int triggerReviewsFetch(int beerId, int page) {
        Timber.v("triggerReviewsFetch(%s)", beerId);

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.BEER_REVIEWS.toString());
        intent.putExtra("listenerId", listenerId);
        intent.putExtra("beerId", beerId);
        intent.putExtra("page", page);
        getContext().startService(intent);

        return listenerId;
    }
}
