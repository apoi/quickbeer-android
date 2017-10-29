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
import quickbeer.android.data.actions.ReviewActions;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.data.stores.ReviewStore;
import quickbeer.android.network.NetworkService;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.fetchers.BeerSearchFetcher;
import quickbeer.android.rx.RxUtils;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;
import timber.log.Timber;

public class ReviewActionsImpl extends ApplicationDataLayer implements ReviewActions {

    @NonNull
    private final NetworkRequestStatusStore requestStatusStore;

    @NonNull
    private final ReviewStore reviewStore;

    @NonNull
    private final BeerListStore beerListStore;

    @Inject
    public ReviewActionsImpl(@NonNull Context context,
                             @NonNull NetworkRequestStatusStore requestStatusStore,
                             @NonNull BeerListStore beerListStore,
                             @NonNull ReviewStore reviewStore) {
        super(context);

        this.requestStatusStore = requestStatusStore;
        this.reviewStore = reviewStore;
        this.beerListStore = beerListStore;
    }

    //// REVIEW

    @Override
    @NonNull
    public Observable<Option<Review>> get(int reviewId) {
        Timber.v("get(%s)", reviewId);

        // Reviews are never fetched one-by-one, only as a list of reviews. This method can only
        // return reviews from the local store, no fetching.
        return reviewStore.getOnceAndStream(reviewId);
    }

    //// USER TICKS

    @Override
    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getTicks(@NonNull String userId) {
        Timber.v("getTicks(%s)", userId);

        return getTicks(userId, list -> list.getItems().isEmpty());
    }

    @Override
    @NonNull
    public Single<Boolean> fetchTicks(@NonNull String userId) {
        Timber.v("fetchTicks(%s)", userId);

        return getTicks(userId, list -> true)
                .filter(DataStreamNotification::isCompleted)
                .map(DataStreamNotification::isCompletedWithSuccess)
                .first()
                .toSingle();
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> getTicks(@NonNull String userId, @NonNull Func1<ItemList<String>, Boolean> needsReload) {
        // Trigger a fetch only if there was no cached result
        Observable<Option<ItemList<String>>> triggerFetchIfEmpty =
                beerListStore.getOnce(BeerSearchFetcher.getQueryId(RateBeerService.USER_TICKS, userId))
                        .toObservable()
                        .filter(option -> option.match(needsReload::call, () -> true))
                        .doOnNext(__ -> {
                            Timber.v("Search not cached, fetching");
                            triggerTicksFetch(userId);
                        });

        return getTicksResultStream(userId)
                .mergeWith(triggerFetchIfEmpty.flatMap(__ -> Observable.empty()));
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> getTicksResultStream(@NonNull String userId) {
        Timber.v("getTicksResultStream");

        String queryId = BeerSearchFetcher.getQueryId(RateBeerService.USER_TICKS, userId);
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

    private int triggerTicksFetch(@NonNull String userId) {
        Timber.v("triggerTicksFetch(%s)", userId);

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.USER_TICKS.toString());
        intent.putExtra("listenerId", listenerId);
        intent.putExtra("userId", userId);
        getContext().startService(intent);

        return listenerId;
    }

    //// USER REVIEWS

    @Override
    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getReviews(@NonNull String userId) {
        Timber.v("getReviews(%s)", userId);

        return getReviews(userId, list -> list.getItems().isEmpty());
    }

    @Override
    @NonNull
    public Single<Boolean> fetchReviews(@NotNull String userId) {
        Timber.v("fetchReviews(%s)", userId);

        return getReviews(userId, list -> true)
                .filter(DataStreamNotification::isCompleted)
                .map(DataStreamNotification::isCompletedWithSuccess)
                .first()
                .toSingle();
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> getReviews(@NonNull String userId, @NonNull Func1<ItemList<String>, Boolean> needsReload) {
        // Trigger a fetch only if there was no cached result
        Observable<Option<ItemList<String>>> triggerFetchIfEmpty =
                beerListStore.getOnce(BeerSearchFetcher.getQueryId(RateBeerService.USER_REVIEWS, userId))
                        .toObservable()
                        .filter(option -> option.match(needsReload::call, () -> true))
                        .doOnNext(__ -> {
                            Timber.v("Search not cached, fetching");
                            triggerReviewFetch(userId);
                        });

        return getReviewsResultStream(userId)
                .mergeWith(triggerFetchIfEmpty.flatMap(__ -> Observable.empty()));
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> getReviewsResultStream(@NonNull String userId) {
        Timber.v("getReviewsResultStream");

        String queryId = BeerSearchFetcher.getQueryId(RateBeerService.USER_REVIEWS, userId);
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

    private int triggerReviewFetch(@NonNull String userId) {
        Timber.v("triggerReviewFetch(%s)", userId);

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.USER_REVIEWS.toString());
        intent.putExtra("listenerId", listenerId);
        intent.putExtra("numReviews", 1);
        intent.putExtra("userId", userId);
        getContext().startService(intent);

        return listenerId;
    }
}
