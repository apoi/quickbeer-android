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
package quickbeer.android.next.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;
import quickbeer.android.next.data.store.UserSettingsStore;
import quickbeer.android.next.network.NetworkService;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.fetchers.TopBeersFetcher;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.BeerSearch;
import quickbeer.android.next.pojo.Review;
import quickbeer.android.next.pojo.ReviewList;
import quickbeer.android.next.pojo.UserSettings;
import rx.Observable;

public class DataLayer extends DataLayerBase {
    private static final String TAG = DataLayer.class.getSimpleName();
    public static final int DEFAULT_USER_ID = 0;

    private final Context context;
    protected final UserSettingsStore userSettingsStore;

    public DataLayer(@NonNull Context context,
                     @NonNull UserSettingsStore userSettingsStore,
                     @NonNull NetworkRequestStatusStore networkRequestStatusStore,
                     @NonNull BeerStore beerStore,
                     @NonNull BeerSearchStore beerSearchStore,
                     @NonNull ReviewStore reviewStore,
                     @NonNull ReviewListStore reviewListStore) {
        super(networkRequestStatusStore, beerStore, beerSearchStore, reviewStore, reviewListStore);

        Preconditions.checkNotNull(context, "Context cannot be null.");
        Preconditions.checkNotNull(userSettingsStore, "User Settings Store cannot be null.");

        this.context = context;
        this.userSettingsStore = userSettingsStore;
    }

    @NonNull
    public Observable<UserSettings> getUserSettings() {
        return userSettingsStore.getStream(DEFAULT_USER_ID);
    }

    public void setUserSettings(@NonNull UserSettings userSettings) {
        Preconditions.checkNotNull(userSettings, "User Settings cannot be null.");

        userSettingsStore.put(userSettings);
    }

    @NonNull
    public Observable<DataStreamNotification<Beer>> getBeerResultStream(@NonNull Integer beerId) {
        Preconditions.checkNotNull(beerId, "Beer id cannot be null.");
        Log.v(TAG, "getBeerResultStream");

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(beerId.toString().hashCode());

        final Observable<Beer> beerObservable =
                beerStore.getStream(beerId);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<Beer>> getBeer(@NonNull Integer beerId) {
        Preconditions.checkNotNull(beerId, "Beer id cannot be null.");
        Log.v(TAG, "getBeer");

        // Trigger a fetch only if full details haven't been fetched
        beerStore.getOne(beerId)
                .first()
                .filter(beer -> beer == null || !beer.hasDetails())
                .doOnNext(beer -> Log.v(TAG, "Beer not cached, fetching"))
                .subscribe(beer -> fetchBeer(beerId));

        return getBeerResultStream(beerId);
    }

    @NonNull
    public Observable<DataStreamNotification<Beer>> fetchAndGetBeer(@NonNull Integer beerId) {
        Preconditions.checkNotNull(beerId, "Beer id cannot be null.");
        Log.v(TAG, "fetchAndGetBeer");

        fetchBeer(beerId);
        return getBeerResultStream(beerId);
    }

    private void fetchBeer(@NonNull Integer beerId) {
        Log.v(TAG, "fetchBeer");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.BEER.toString());
        intent.putExtra("id", beerId);
        context.startService(intent);
    }

    @NonNull
    public Observable<List<String>> getBeerSearchQueries() {
        Log.v(TAG, "getBeerSearchQueries");

        return beerSearchStore.get("")
                .map(beerSearches -> {
                    List<String> searches = new ArrayList<>();
                    for (BeerSearch search : beerSearches) {
                        // Filter out meta searches, such as __top50
                        if (!search.getSearch().startsWith("__")) {
                            searches.add(search.getSearch());
                        }
                    }
                    return searches;
                });
    }

    @NonNull
    public Observable<DataStreamNotification<BeerSearch>> getBeerSearchResultStream(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string cannot be null.");
        Log.v(TAG, "getBeerSearchResultStream");

        final Uri uri = beerSearchStore.getUriForId(searchString);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());

        final Observable<BeerSearch> beerSearchObservable =
                beerSearchStore.getStream(searchString);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerSearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<BeerSearch>> getBeerSearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string cannot be null.");
        Log.v(TAG, "getBeerSearch");

        // Trigger a fetch only if there was no cached result
        beerSearchStore.getOne(searchString)
                .first()
                .filter(results -> results == null || results.getItems().size() == 0)
                .doOnNext(results -> Log.v(TAG, "Search not cached, fetching"))
                .subscribe(results -> fetchBeerSearch(searchString));

        return getBeerSearchResultStream(searchString);
    }

    @NonNull
    public Observable<DataStreamNotification<BeerSearch>> fetchAndGetBeerSearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string cannot be null.");
        Log.v(TAG, "fetchAndGetBeerSearch");

        fetchBeerSearch(searchString);
        return getBeerSearchResultStream(searchString);
    }

    private void fetchBeerSearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string cannot be null.");
        Log.v(TAG, "fetchBeerSearch");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.SEARCH.toString());
        intent.putExtra("searchString", searchString);
        context.startService(intent);
    }

    @NonNull
    public Observable<DataStreamNotification<BeerSearch>> getTopBeersResultStream() {
        Log.v(TAG, "getTopBeersResultStream");

        final Uri uri = beerSearchStore.getUriForId(TopBeersFetcher.SEARCH);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());

        final Observable<BeerSearch> beerSearchObservable =
                beerSearchStore.getStream(TopBeersFetcher.SEARCH);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerSearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<BeerSearch>> getTopBeers() {
        Log.v(TAG, "getTopBeers");

        // Trigger a fetch only if there was no cached result
        beerSearchStore.getOne(RateBeerService.TOP50.toString())
                .first()
                .filter(results -> results == null || results.getItems().size() == 0)
                .doOnNext(results -> Log.v(TAG, "Search not cached, fetching"))
                .subscribe(results -> fetchTopBeers());

        return getTopBeersResultStream();
    }

    @NonNull
    public Observable<DataStreamNotification<BeerSearch>> fetchAndGetTopBeers() {
        Log.v(TAG, "fetchAndGetTopBeers");

        fetchTopBeers();
        return getTopBeersResultStream();
    }

    private void fetchTopBeers() {
        Log.v(TAG, "fetchTopBeers");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.TOP50.toString());
        context.startService(intent);
    }

    @NonNull
    public Observable<Review> getReview(final int reviewId) {
        Log.v(TAG, "getReview(" + reviewId + ")");

        // Reviews are never fetched one-by-one, only as a list of reviews. This method can only
        // return reviews from the local store, no fetching.
        return reviewStore.getStream(reviewId);
    }

    @NonNull
    public Observable<DataStreamNotification<ReviewList>> getReviewsResultStream(final int beerId) {
        Log.v(TAG, "getReviewsResultStream(" + beerId + ")");

        final Uri uri = reviewListStore.getUriForId(beerId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());

        final Observable<ReviewList> reviewListObservable =
                reviewListStore.getStream(beerId);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, reviewListObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<ReviewList>> getReviews(final int beerId) {
        Log.v(TAG, "getReviews(" + beerId + ")");

        // Trigger a fetch only if there was no cached result
        reviewListStore.getOne(beerId)
                .first()
                .filter(results -> results == null || results.getItems().size() == 0)
                .doOnNext(results -> Log.v(TAG, "Reviews not cached, fetching"))
                .subscribe(results -> fetchReviews(beerId));

        return getReviewsResultStream(beerId);
    }

    private void fetchReviews(final int beerId) {
        Log.v(TAG, "fetchReviews(" + beerId + ")");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.REVIEWS.toString());
        intent.putExtra("beerId", beerId);
        context.startService(intent);
    }

    public interface GetUserSettings {
        @NonNull
        Observable<UserSettings> call();
    }

    public interface SetUserSettings {
        void call(@NonNull UserSettings userSettings);
    }

    public interface GetBeer {
        @NonNull
        Observable<DataStreamNotification<Beer>> call(int beerId);
    }

    public interface GetBeerSearchQueries {
        @NonNull
        Observable<List<String>> call();
    }

    public interface GetBeerSearch {
        @NonNull
        Observable<DataStreamNotification<BeerSearch>> call(@NonNull String search);
    }

    public interface GetTopBeers {
        @NonNull
        Observable<DataStreamNotification<BeerSearch>> call();
    }

    public interface GetReview {
        @NonNull
        Observable<Review> call(int reviewId);
    }

    public interface GetReviews {
        @NonNull
        Observable<DataStreamNotification<ReviewList>> call(int beerId);
    }
}
