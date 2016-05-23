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
import java.util.Date;
import java.util.List;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.store.BeerListStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.BrewerListStore;
import quickbeer.android.next.data.store.BrewerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;
import quickbeer.android.next.data.store.UserSettingsStore;
import quickbeer.android.next.network.NetworkService;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.Brewer;
import quickbeer.android.next.pojo.ItemList;
import quickbeer.android.next.pojo.Review;
import quickbeer.android.next.pojo.UserSettings;
import quickbeer.android.next.rx.DistinctiveTracker;
import quickbeer.android.next.rx.NullFilter;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class DataLayer extends DataLayerBase {
    private static final String TAG = DataLayer.class.getSimpleName();

    private final Context context;
    private final UserSettingsStore userSettingsStore;

    public DataLayer(@NonNull Context context,
                     @NonNull UserSettingsStore userSettingsStore,
                     @NonNull NetworkRequestStatusStore networkRequestStatusStore,
                     @NonNull BeerStore beerStore,
                     @NonNull BeerListStore beerListStore,
                     @NonNull ReviewStore reviewStore,
                     @NonNull ReviewListStore reviewListStore,
                     @NonNull BrewerStore brewerStore,
                     @NonNull BrewerListStore brewerListStore) {
        super(networkRequestStatusStore,
                beerStore, beerListStore,
                reviewStore, reviewListStore,
                brewerStore, brewerListStore);

        Preconditions.checkNotNull(context, "Context cannot be null.");
        Preconditions.checkNotNull(userSettingsStore, "User settings store cannot be null.");

        this.context = context;
        this.userSettingsStore = userSettingsStore;
    }

    //// GET USER SETTINGS

    @NonNull
    public Observable<UserSettings> getUserSettings() {
        return userSettingsStore.getStream();
    }

    public void setUserSettings(@NonNull UserSettings userSettings) {
        Preconditions.checkNotNull(userSettings, "User settings cannot be null.");

        userSettingsStore.put(userSettings);
    }

    //// LOGIN

    public Observable<Boolean> login(@NonNull String username,
                                     @NonNull String password) {
        Preconditions.checkNotNull(username, "Username cannot be null.");
        Preconditions.checkNotNull(password, "Password cannot be null.");
        Log.v(TAG, "login");

        Func0<Observable<Boolean>> doLogin = () -> {
            final int userId = UserSettingsStore.DEFAULT_USER_ID;
            final int id = userSettingsStore.getUriForId(userId).toString().hashCode();

            final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                    networkRequestStatusStore.getStream(id);

            Intent intent = new Intent(context, NetworkService.class);
            intent.putExtra("serviceUriString", RateBeerService.LOGIN.toString());
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            context.startService(intent);

            // Observe the success of the network request. Completion means we logged in
            // successfully, while login wasn't successful on error.
            return networkRequestStatusObservable
                    .filter(networkRequestStatus ->
                            networkRequestStatus.isCompleted()
                                    || networkRequestStatus.isError())
                    .first()
                    .map(NetworkRequestStatus::isCompleted);
        };

        // Updates the settings if needed, and performs login if we're not already logged,
        // or if the used credentials changed.
        return getUserSettings()
                .filter(new NullFilter())
                .first()
                .map(userSettings -> {
                    if (!userSettings.credentialsEqual(username, password)) {
                        userSettings.setUsername(username);
                        userSettings.setPassword(password);
                        userSettings.setIsLogged(false);
                        userSettingsStore.put(userSettings);
                    }
                    return userSettings;
                })
                .flatMap(userSettings -> {
                    return userSettings.isLogged()
                            ? Observable.just(true)
                            : doLogin.call();
                });
    }

    //// GET BEER DETAILS

    @NonNull
    public Observable<DataStreamNotification<Beer>> getBeerResultStream(@NonNull Integer beerId) {
        Preconditions.checkNotNull(beerId, "Beer id cannot be null.");
        Log.v(TAG, "getBeerResultStream");

        final Uri uri = beerStore.getUriForId(beerId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());

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

        // Does not emit a new notification when only beer metadata changes.
        // This avoids unnecessary view redraws.
        return getBeerResultStream(beerId)
                .distinctUntilChanged(new DistinctiveTracker<Beer>());
    }

    private void fetchBeer(@NonNull Integer beerId) {
        Log.v(TAG, "fetchBeer");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.BEER.toString());
        intent.putExtra("id", beerId);
        context.startService(intent);
    }

    //// ACCESS BEER

    public void accessBeer(@NonNull Integer beerId) {
        Preconditions.checkNotNull(beerId, "Beer id cannot be null.");
        Log.v(TAG, "accessBeer(" + beerId + ")");

        beerStore.getOne(beerId)
                .observeOn(Schedulers.computation())
                .first()
                .filter(new NullFilter())
                .map(beer -> {
                    beer.setAccessDate(new Date());
                    return beer;
                })
                .subscribe(
                        beerStore::put,
                        throwable -> Log.e(TAG, "Error updating beer access date:", throwable)
                );
    }

    //// ACCESSED BEERS

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getAccessedBeers() {
        Log.v(TAG, "getAccessedBeers");

        // Function to move/add new item to the top of a list
        Func2<List<Integer>, Integer, List<Integer>> mergeList = (integers, integer) -> {
            int index = integers.indexOf(integer);
            if (index > 0) integers.remove(index);
            if (index != 0) integers.add(0, integer);
            return integers;
        };

        // Caching subject for keeping a list of accessed beers without querying the database
        BehaviorSubject<List<Integer>> subject = BehaviorSubject.create();

        // Observing the stores and updating the caching subject
        beerStore.getAccessedIds()
                .doOnNext(ids -> Log.d(TAG, "getAccessedBeers: initial of " + ids.size()))
                .doOnNext(subject::onNext)
                .flatMap(ids -> beerStore.getNewlyAccessedIds(new Date())
                        .doOnNext(id -> Log.d(TAG, "getAccessedBeers: accessed " + id))
                        .map(id -> mergeList.call(subject.getValue(), id))
                )
                .subscribe(subject::onNext);

        // Convert the subject to a stream similar to beer searches
        return subject.asObservable()
                .doOnNext(ids -> Log.d(TAG, "getAccessedBeers: list now " + ids.size()))
                .map(ids -> new ItemList<String>(null, ids, null))
                .map(DataStreamNotification::onNext);
    }

    //// SEARCH BEERS

    @NonNull
    public Observable<List<String>> getBeerSearchQueries() {
        Log.v(TAG, "getBeerSearchQueries");

        return beerListStore.get("")
                .map(beerSearches -> {
                    List<String> searches = new ArrayList<>();
                    for (ItemList<String> search : beerSearches) {
                        // Filter out meta searches, such as __top50
                        if (!search.getKey().startsWith("__")) {
                            searches.add(search.getKey());
                        }
                    }
                    return searches;
                });
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getBeerSearchResultStream(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string cannot be null.");
        Log.v(TAG, "getBeerSearchResultStream");

        final String queryId = beerListStore.getQueryId(RateBeerService.SEARCH, searchString);
        final Uri uri = beerListStore.getUriForId(queryId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());

        final Observable<ItemList<String>> beerSearchObservable =
                beerListStore.getStream(queryId);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerSearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getBeerSearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string cannot be null.");
        Log.v(TAG, "getBeerSearch");

        // Trigger a fetch only if there was no cached result
        beerListStore.getOne(beerListStore.getQueryId(RateBeerService.SEARCH, searchString))
                .first()
                .filter(results -> results == null || results.getItems().size() == 0)
                .doOnNext(results -> Log.v(TAG, "Search not cached, fetching"))
                .subscribe(results -> fetchBeerSearch(searchString));

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

    //// TOP BEERS

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getTopBeersResultStream() {
        Log.v(TAG, "getTopBeersResultStream");

        final String queryId = beerListStore.getQueryId(RateBeerService.TOP50);
        final Uri uri = beerListStore.getUriForId(queryId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());

        final Observable<ItemList<String>> beerSearchObservable =
                beerListStore.getStream(queryId);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerSearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getTopBeers() {
        Log.v(TAG, "getTopBeers");

        // Trigger a fetch only if there was no cached result
        beerListStore.getOne(beerListStore.getQueryId(RateBeerService.TOP50))
                .first()
                .filter(results -> results == null || results.getItems().size() == 0)
                .doOnNext(results -> Log.v(TAG, "Search not cached, fetching"))
                .subscribe(results -> fetchTopBeers());

        return getTopBeersResultStream();
    }

    private void fetchTopBeers() {
        Log.v(TAG, "fetchTopBeers");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.TOP50.toString());
        context.startService(intent);
    }

    //// BEERS IN COUNTRY

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getBeersInCountryResultStream(@NonNull final String countryId) {
        Log.v(TAG, "getBeersInCountryResultStream");

        final String queryId = beerListStore.getQueryId(RateBeerService.COUNTRY, countryId);
        final Uri uri = beerListStore.getUriForId(queryId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());

        final Observable<ItemList<String>> beerSearchObservable =
                beerListStore.getStream(queryId);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerSearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getBeersInCountry(@NonNull final String countryId) {
        Log.v(TAG, "getBeersInCountry");

        // Trigger a fetch only if there was no cached result
        beerListStore.getOne(beerListStore.getQueryId(RateBeerService.COUNTRY, countryId))
                .first()
                .filter(results -> results == null || results.getItems().size() == 0)
                .doOnNext(results -> Log.v(TAG, "Search not cached, fetching"))
                .subscribe(results -> fetchBeersInCountry(countryId));

        return getBeersInCountryResultStream(countryId);
    }

    private void fetchBeersInCountry(@NonNull final String countryId) {
        Log.v(TAG, "fetchBeersInCountry");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.COUNTRY.toString());
        intent.putExtra("countryId", countryId);
        context.startService(intent);
    }

    //// BEERS IN STYLE

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getBeersInStyleResultStream(@NonNull final String styleId) {
        Log.v(TAG, "getBeersInStyleResultStream");

        final String queryId = beerListStore.getQueryId(RateBeerService.STYLE, styleId);
        final Uri uri = beerListStore.getUriForId(queryId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());

        final Observable<ItemList<String>> beerSearchObservable =
                beerListStore.getStream(queryId);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerSearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getBeersInStyle(@NonNull final String styleId) {
        Log.v(TAG, "getBeersInStyle");

        // Trigger a fetch only if there was no cached result
        beerListStore.getOne(beerListStore.getQueryId(RateBeerService.STYLE, styleId))
                .first()
                .filter(results -> results == null || results.getItems().size() == 0)
                .doOnNext(results -> Log.v(TAG, "Search not cached, fetching"))
                .subscribe(results -> fetchBeersInStyle(styleId));

        return getBeersInStyleResultStream(styleId);
    }

    private void fetchBeersInStyle(@NonNull final String styleId) {
        Log.v(TAG, "fetchBeersInStyle");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.STYLE.toString());
        intent.putExtra("styleId", styleId);
        context.startService(intent);
    }

    //// REVIEWS

    @NonNull
    public Observable<Review> getReview(final int reviewId) {
        Log.v(TAG, "getReview(" + reviewId + ")");

        // Reviews are never fetched one-by-one, only as a list of reviews. This method can only
        // return reviews from the local store, no fetching.
        return reviewStore.getStream(reviewId);
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<Integer>>> getReviewsResultStream(final int beerId) {
        Log.v(TAG, "getReviewsResultStream(" + beerId + ")");

        final Uri uri = reviewListStore.getUriForId(beerId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());

        final Observable<ItemList<Integer>> reviewListObservable =
                reviewListStore.getStream(beerId);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, reviewListObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<Integer>>> getReviews(final int beerId) {
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

    //// TICKS

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getTickedBeers(@NonNull final String userId) {
        Log.v(TAG, "getTickedBeers");

        fetchTickedBeers(userId);

        // TODO need to trigger update on the query when new ticked beers are inserted
        return beerStore.getTickedIds()
                .map(ids -> new ItemList<String>(null, ids, null))
                .map(DataStreamNotification::onNext);
    }

    private void fetchTickedBeers(@NonNull final String userId) {
        Log.v(TAG, "fetchTickedBeers");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.TICKS.toString());
        intent.putExtra("userId", userId);
        context.startService(intent);
    }

    //// GET BREWER DETAILS

    @NonNull
    public Observable<DataStreamNotification<Brewer>> getBrewerResultStream(@NonNull Integer brewerId) {
        Preconditions.checkNotNull(brewerId, "Brewer id cannot be null.");
        Log.v(TAG, "getBrewerResultStream");

        final Uri uri = brewerStore.getUriForId(brewerId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());

        final Observable<Brewer> brewerObservable =
                brewerStore.getStream(brewerId);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, brewerObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<Brewer>> getBrewer(@NonNull Integer brewerId) {
        Preconditions.checkNotNull(brewerId, "Brewer id cannot be null.");
        Log.v(TAG, "getBrewer");

        // Trigger a fetch only if full details haven't been fetched
        beerStore.getOne(brewerId)
                .first()
                .filter(brewer -> brewer == null || !brewer.hasDetails())
                .doOnNext(beer -> Log.v(TAG, "Brewer not cached, fetching"))
                .subscribe(beer -> fetchBrewer(brewerId));

        // Does not emit a new notification when only beer metadata changes.
        // This avoids unnecessary view redraws.
        return getBrewerResultStream(brewerId)
                .distinctUntilChanged(new DistinctiveTracker<Brewer>());
    }

    private void fetchBrewer(@NonNull Integer brewerId) {
        Log.v(TAG, "fetchBrewer");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.BREWER.toString());
        intent.putExtra("id", brewerId);
        context.startService(intent);
    }

    //// ACCESS BREWER

    public void accessBrewer(@NonNull Integer brewerId) {
        Preconditions.checkNotNull(brewerId, "Brewer id cannot be null.");
        Log.v(TAG, "accessBrewer(" + brewerId + ")");

        brewerStore.getOne(brewerId)
                .observeOn(Schedulers.computation())
                .first()
                .filter(new NullFilter())
                .map(brewer -> {
                    brewer.setAccessDate(new Date());
                    return brewer;
                })
                .subscribe(
                        brewerStore::put,
                        throwable -> Log.e(TAG, "Error updating brewer access date:", throwable)
                );
    }

    //// ACCESSED BREWERS

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getAccessedBrewers() {
        Log.v(TAG, "getAccessedBrewers");

        // Function to move/add new item to the top of a list
        Func2<List<Integer>, Integer, List<Integer>> mergeList = (integers, integer) -> {
            int index = integers.indexOf(integer);
            if (index > 0) integers.remove(index);
            if (index != 0) integers.add(0, integer);
            return integers;
        };

        // Caching subject for keeping a list of accessed brewers without querying the database
        BehaviorSubject<List<Integer>> subject = BehaviorSubject.create();

        // Observing the stores and updating the caching subject
        brewerStore.getAccessedIds()
                .doOnNext(ids -> Log.d(TAG, "getAccessedBrewers: initial of " + ids.size()))
                .doOnNext(subject::onNext)
                .flatMap(ids -> brewerStore.getNewlyAccessedIds(new Date())
                        .doOnNext(id -> Log.d(TAG, "getAccessedBrewers: accessed " + id))
                        .map(id -> mergeList.call(subject.getValue(), id))
                )
                .subscribe(subject::onNext);

        // Convert the subject to a stream similar to beer searches
        return subject.asObservable()
                .doOnNext(ids -> Log.d(TAG, "getAccessedBrewers: list now " + ids.size()))
                .map(ids -> new ItemList<String>(null, ids, null))
                .map(DataStreamNotification::onNext);
    }

    public interface Login {
        @NonNull
        Observable<Boolean> call(@NonNull String username, @NonNull String password);
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

    public interface AccessBeer {

        void call(int beerId);
    }

    public interface GetAccessedBeers {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call();
    }

    public interface GetBeerSearchQueries {
        @NonNull
        Observable<List<String>> call();
    }

    public interface GetBeerSearch {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call(@NonNull String search);
    }

    public interface GetTopBeers {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call();
    }

    public interface GetBeersInCountry {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call(@NonNull String countryId);
    }

    public interface GetBeersInStyle {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call(@NonNull String styleId);
    }

    public interface GetReview {
        @NonNull
        Observable<Review> call(int reviewId);
    }

    public interface GetReviews {
        @NonNull
        Observable<DataStreamNotification<ItemList<Integer>>> call(int beerId);
    }

    public interface GetTickedBeers {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call(String userId);
    }

    public interface GetBrewer {
        @NonNull
        Observable<DataStreamNotification<Brewer>> call(int brewerId);
    }

    public interface AccessBrewer {
        void call(int beerId);
    }

    public interface GetAccessedBrewers {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call();
    }
}
