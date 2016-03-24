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
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.utils.NetworkUtils;
import quickbeer.android.next.pojo.Review;
import quickbeer.android.next.pojo.ReviewList;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ReviewFetcher extends FetcherBase {
    private static final String TAG = ReviewFetcher.class.getSimpleName();

    private final NetworkApi networkApi;
    private final NetworkUtils networkUtils;
    private final ReviewStore reviewStore;
    private final ReviewListStore reviewListStore;

    public ReviewFetcher(@NonNull NetworkApi networkApi,
                         @NonNull NetworkUtils networkUtils,
                         @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                         @NonNull ReviewStore reviewStore,
                         @NonNull ReviewListStore reviewListStore) {
        super(updateNetworkRequestStatus);

        Preconditions.checkNotNull(networkApi, "Network api cannot be null.");
        Preconditions.checkNotNull(networkUtils, "Network utils cannot be null.");
        Preconditions.checkNotNull(reviewStore, "Review store cannot be null.");
        Preconditions.checkNotNull(reviewListStore, "Review list store cannot be null.");

        this.networkApi = networkApi;
        this.networkUtils = networkUtils;
        this.reviewStore = reviewStore;
        this.reviewListStore = reviewListStore;
    }

    @Override
    public void fetch(@NonNull Intent intent) {
        final int beerId = intent.getIntExtra("beerId", -1);

        if (beerId > 0) {
            fetchReviews(beerId);
        } else {
            Log.e(TAG, "No beerId provided in the intent extras");
        }
    }

    protected void fetchReviews(final int beerId) {
        Log.d(TAG, "fetchReviews(" + beerId + ")");

        if (requestMap.containsKey(beerId) &&
                !requestMap.get(beerId).isUnsubscribed()) {
            Log.d(TAG, "Found an ongoing request for reviews " + beerId);
            return;
        }

        final String uri = reviewListStore.getUriForId(beerId).toString();
        Subscription subscription = createNetworkObservable(beerId)
                .subscribeOn(Schedulers.computation())
                .map((reviews) -> {
                    final List<Integer> reviewIds = new ArrayList<>();
                    for (Review review : reviews) {
                        reviewStore.put(review);
                        reviewIds.add(review.getId());
                    }
                    return new ReviewList(beerId, reviewIds, new Date());
                })
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .subscribe(reviewListStore::put,
                        e -> Log.e(TAG, "Error fetching reviews for '" + beerId + "'", e));

        requestMap.put(beerId, subscription);
        startRequest(uri);
    }

    @NonNull
    protected Observable<List<Review>> createNetworkObservable(final int beerId) {
        return networkApi.getReviews(networkUtils.createRequestParams("bid", String.valueOf(beerId)));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.REVIEWS;
    }
}
