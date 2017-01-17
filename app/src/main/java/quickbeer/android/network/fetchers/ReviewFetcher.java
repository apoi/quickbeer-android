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
package quickbeer.android.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.List;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.data.stores.ReviewStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.NetworkUtils;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.pojos.Review;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static io.reark.reark.utils.Preconditions.get;

public class ReviewFetcher extends FetcherBase<Uri> {
    private static final String TAG = ReviewFetcher.class.getSimpleName();

    @NonNull
    private final NetworkApi networkApi;

    @NonNull
    private final NetworkUtils networkUtils;

    @NonNull
    private final ReviewStore reviewStore;

    @NonNull
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
                .flatMap(Observable::from)
                .doOnNext(reviewStore::put)
                .map(Review::id)
                .toList()
                .map(reviewIds -> ItemList.create(beerId, reviewIds, DateTime.now()))
                .doOnSubscribe(() -> startRequest(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .subscribe(reviewListStore::put,
                        Log.onError(TAG, "Error fetching reviews for beer " + beerId));

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
