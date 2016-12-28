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
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.utils.NetworkUtils;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.ItemList;
import quickbeer.android.next.pojo.Review;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static io.reark.reark.utils.Preconditions.get;

public class ReviewFetcher extends FetcherBase<Uri> {
    private static final String TAG = ReviewFetcher.class.getSimpleName();

    private final NetworkApi networkApi;
    private final NetworkUtils networkUtils;
    private final ReviewStore reviewStore;
    private final ReviewListStore reviewListStore;

    public ReviewFetcher(@NonNull final NetworkApi networkApi,
                         @NonNull final NetworkUtils networkUtils,
                         @NonNull final Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                         @NonNull final ReviewStore reviewStore,
                         @NonNull final ReviewListStore reviewListStore) {
        super(updateNetworkRequestStatus);

        this.networkApi = get(networkApi);
        this.networkUtils = get(networkUtils);
        this.reviewStore = get(reviewStore);
        this.reviewListStore = get(reviewListStore);
    }

    @Override
    public void fetch(@NonNull final Intent intent) {
        final int beerId = intent.getIntExtra("beerId", -1);

        if (beerId > 0) {
            fetchReviews(beerId);
        } else {
            Log.e(TAG, "No beerId provided in the intent extras");
        }
    }

    private void fetchReviews(final int beerId) {
        Log.d(TAG, "fetchReviews(" + beerId + ")");

        if (isOngoingRequest(beerId)) {
            Log.d(TAG, "Found an ongoing request for reviews for beer " + beerId);
            return;
        }

        final String uri = getUniqueUri(beerId);

        Subscription subscription = createNetworkObservable(beerId)
                .subscribeOn(Schedulers.computation())
                .map((reviews) -> {
                    final List<Integer> reviewIds = new ArrayList<>(10);
                    for (final Review review : reviews) {
                        reviewStore.put(review);
                        reviewIds.add(review.getId());
                    }
                    return new ItemList<>(beerId, reviewIds, new Date());
                })
                .doOnSubscribe(() -> startRequest(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .subscribe(reviewListStore::put,
                           e -> Log.e(TAG, "Error fetching reviews for beer " + beerId, e));

        addRequest(beerId, subscription);
    }

    @NonNull
    private Observable<List<Review>> createNetworkObservable(final int beerId) {
        return networkApi.getReviews(networkUtils.createRequestParams("bid", String.valueOf(beerId)));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.REVIEWS;
    }

    @NonNull
    public static String getUniqueUri(final int id) {
        return Review.class + "/" + id;
    }
}
