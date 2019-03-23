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
import androidx.annotation.NonNull;

import org.threeten.bp.ZonedDateTime;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.data.stores.ReviewStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.NetworkUtils;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class ReviewFetcher extends FetcherBase<Uri> {

    @NonNull
    private final NetworkApi networkApi;

    @NonNull
    private final NetworkUtils networkUtils;

    @NonNull
    private final ReviewStore reviewStore;

    @NonNull
    private final ReviewListStore reviewListStore;

    public ReviewFetcher(@NonNull NetworkApi networkApi,
                         @NonNull NetworkUtils networkUtils,
                         @NonNull Consumer<NetworkRequestStatus> networkRequestStatus,
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

        if (!intent.hasExtra("beerId")) {
            Timber.e("Missing required fetch parameters!");
            return;
        }

        int beerId = intent.getIntExtra("beerId", 0);
        int page = intent.getIntExtra("page", 1);
        String uri = getUniqueUri(beerId);

        addListener(beerId, listenerId);

        if (isOngoingRequest(beerId)) {
            Timber.d("Found an ongoing request for reviews for beer " + beerId);
            return;
        }

        Timber.d("fetchReviews(%s, %s)", beerId, page);

        Disposable disposable = createNetworkObservable(beerId, page)
                .subscribeOn(Schedulers.io())
                .flatMapObservable(Observable::fromIterable)
                .flatMapSingle(review -> reviewStore.put(review).map(__ -> review))
                .toList()
                .map(ReviewFetcher::sort)
                .flatMapObservable(Observable::fromIterable)
                .map(Review::getId)
                .toList()
                .map(reviewIds -> ItemList.Companion.create(beerId, reviewIds, ZonedDateTime.now()))
                .flatMap(reviewListStore::put)
                .doOnSubscribe(__ -> startRequest(beerId, uri))
                .doOnSuccess(updated -> completeRequest(beerId, uri, updated))
                .doOnError(doOnError(beerId, uri))
                .subscribe(Functions.emptyConsumer(),
                        error -> Timber.w(error, "Error fetching reviews for beer %s", beerId));

        addRequest(beerId, disposable);
    }

    @SuppressWarnings("IfMayBeConditional")
    @NonNull
    protected static List<Review> sort(@NonNull List<Review> list) {
        Collections.sort(list, (first, second) -> {
            ZonedDateTime firstDate = first.getTimeEntered();
            ZonedDateTime secondDate = second.getTimeEntered();

            if (firstDate == null) {
                if (secondDate == null) {
                    return Integer.valueOf(second.getId()).compareTo(first.getId());
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
    private Single<List<Review>> createNetworkObservable(int beerId, int page) {
        Map<String, String> params = networkUtils.createRequestParams("bid", String.valueOf(beerId));
        params.put("p", String.valueOf(page));

        return networkApi.getReviews(params);
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.BEER_REVIEWS;
    }

    @NonNull
    public static String getUniqueUri(int id) {
        return Review.class + "/" + id;
    }
}
