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

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import quickbeer.android.Constants;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.data.stores.ReviewStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.NetworkUtils;
import rx.Observable;
import rx.Single;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class ReviewsFetcher extends FetcherBase<Uri> {

    @NonNull
    protected final NetworkApi networkApi;

    @NonNull
    protected final NetworkUtils networkUtils;

    @NonNull
    private final ReviewStore reviewStore;

    @NonNull
    private final ReviewListStore reviewListStore;

    public ReviewsFetcher(@NonNull NetworkApi networkApi,
                          @NonNull NetworkUtils networkUtils,
                          @NonNull Action1<NetworkRequestStatus> networkRequestStatus,
                          @NonNull ReviewStore reviewStore,
                          @NonNull ReviewListStore reviewListStore) {
        super(networkRequestStatus);

        this.networkApi = get(networkApi);
        this.networkUtils = get(networkUtils);
        this.reviewStore = get(reviewStore);
        this.reviewListStore = get(reviewListStore);
    }

    @Override
    public void fetch(@NonNull Intent intent, int listenerId) {
        checkNotNull(intent);

        if (!intent.hasExtra("numReviews") || !intent.hasExtra("userId")) {
            Timber.e("Missing required fetch parameters!");
            return;
        }

        String userId = get(intent).getStringExtra("userId");
        int numReviews = get(intent).getIntExtra("numReviews", 0);

        fetchReviewedBeers(userId, numReviews, listenerId);
    }

    private void fetchReviewedBeers(@NonNull String userId, int numReviews, int listenerId) {
        Timber.d("fetchReviewedBeers(" + numReviews + ")");

        String uri = getUniqueUri(userId);
        int queryId = userId.hashCode();
        int requestId = uri.hashCode();

        addListener(requestId, listenerId);

        if (isOngoingRequest(requestId)) {
            Timber.d("Found an ongoing request for search " + queryId);
            return;
        }

        Subscription subscription = getReviews(1, numReviews)
                .map(ReviewsFetcher::sortByReviewDate)
                .toObservable()
                .flatMap(Observable::from)
                .map(Review::getCountryID)
                .toList()
                .toSingle()
                .map(reviewIds -> ItemList.Companion.create(queryId, reviewIds, ZonedDateTime.now()))
                .flatMap(reviewListStore::put)
                .doOnSubscribe(() -> startRequest(requestId, uri))
                .doOnSuccess(updated -> completeRequest(requestId, uri, updated))
                .doOnError(doOnError(requestId, uri))
                .subscribe(Actions.empty(),
                        error -> Timber.w(error, "Error fetching reviews for user %s", userId));

        addRequest(requestId, subscription);
    }

    @NonNull
    private Single<List<Review>> getReviews(int page, int numReviews) {
        return createNetworkObservable(String.valueOf(page))
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMap(Observable::from)
                .flatMap(review -> reviewStore.put(review).map(__ -> review).toObservable())
                .toList()
                .toSingle()
                .zipWith(chooseNextAction(page + 1, numReviews), ReviewsFetcher::joinReviews);
    }

    @NonNull
    private Single<List<Review>> chooseNextAction(int page, int numReviews) {
        if (page <= Math.ceil(numReviews / (float) Constants.USER_REVIEWS_PER_PAGE)) {
            return getReviews(page, numReviews);
        } else {
            return Single.just(Collections.emptyList());
        }
    }

    @NonNull
    private static List<Review> joinReviews(List<Review> reviews, List<Review> reviews2) {
        List<Review> list = new ArrayList<>(reviews);
        list.addAll(reviews2);
        return list;
    }

    @SuppressWarnings("IfMayBeConditional")
    @NonNull
    protected static List<Review> sortByReviewDate(@NonNull List<Review> list) {
        Collections.sort(list, (first, second) -> {
            ZonedDateTime firstDate = first.getTimeUpdated();
            ZonedDateTime secondDate = second.getTimeUpdated();

            if (firstDate == null) {
                if (secondDate == null) {
                    return Integer.valueOf(first.getId()).compareTo(second.getId());
                } else {
                    return -1;
                }
            } else {
                if (secondDate == null) {
                    return 1;
                } else {
                    return secondDate.compareTo(firstDate);
                }
            }
        });

        return list;
    }

    @NonNull
    protected Single<List<Review>> createNetworkObservable(@NonNull String page) {
        Map<String, String> params = networkUtils.createRequestParams("m", "BR");
        params.put("p", page);

        return networkApi.getUserReviews(params);
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.USER_REVIEWS;
    }

    @NonNull
    public static String getUniqueUri(@NonNull String userId) {
        return ItemList.class + "/reviews/" + userId;
    }
}
