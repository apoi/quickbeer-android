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
package quickbeer.android.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.List;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import polanski.option.Option;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.data.pojos.UserSettings;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.data.stores.BrewerListStore;
import quickbeer.android.data.stores.BrewerMetadataStore;
import quickbeer.android.data.stores.BrewerStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.data.stores.ReviewStore;
import quickbeer.android.data.stores.UserSettingsStore;
import quickbeer.android.network.NetworkService;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.fetchers.BeerFetcher;
import quickbeer.android.network.fetchers.BeerSearchFetcher;
import quickbeer.android.network.fetchers.BrewerFetcher;
import quickbeer.android.network.fetchers.LoginFetcher;
import quickbeer.android.network.fetchers.ReviewFetcher;
import quickbeer.android.rx.RxUtils;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;
import static quickbeer.android.data.stores.NetworkRequestStatusStore.requestIdForUri;

public class DataLayer extends DataLayerBase {
    private static final String TAG = DataLayer.class.getSimpleName();

    private final Context context;
    private final UserSettingsStore userSettingsStore;

    public DataLayer(@NonNull final Context context,
                     @NonNull final UserSettingsStore userSettingsStore,
                     @NonNull final NetworkRequestStatusStore networkRequestStatusStore,
                     @NonNull final BeerStore beerStore,
                     @NonNull final BeerListStore beerListStore,
                     @NonNull final BeerMetadataStore beerMetadataStore,
                     @NonNull final ReviewStore reviewStore,
                     @NonNull final ReviewListStore reviewListStore,
                     @NonNull final BrewerStore brewerStore,
                     @NonNull final BrewerListStore brewerListStore,
                     @NonNull final BrewerMetadataStore brewerMetadataStore) {
        super(networkRequestStatusStore,
                beerStore, beerListStore, beerMetadataStore,
                reviewStore, reviewListStore,
                brewerStore, brewerListStore, brewerMetadataStore);

        this.context = get(context);
        this.userSettingsStore = get(userSettingsStore);
    }

    //// GET USER SETTINGS

    @NonNull
    public Observable<Option<UserSettings>> getUserSettings() {
        return userSettingsStore.getOnceAndStream(UserSettingsStore.DEFAULT_USER_ID);
    }

    public void setUserSettings(@NonNull final UserSettings userSettings) {
        userSettingsStore.put(get(userSettings));
    }

    //// LOGIN

    public Observable<Boolean> login(@NonNull final String username,
                                     @NonNull final String password) {
        checkNotNull(username);
        checkNotNull(password);

        Log.v(TAG, "login");

        Func0<Observable<Boolean>> doLogin = () -> {
            Intent intent = new Intent(context, NetworkService.class);
            intent.putExtra("serviceUriString", RateBeerService.LOGIN.toString());
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            context.startService(intent);

            // Observe the success of the network request. Completion means we logged in
            // successfully, while on error the login wasn't successful.
            return networkRequestStatusStore
                    .getOnceAndStream(requestIdForUri(LoginFetcher.getUniqueUri()))
                    .compose(RxUtils::pickValue)
                    .filter(networkRequestStatus -> networkRequestStatus.isCompleted() || networkRequestStatus.isError())
                    .first()
                    .map(NetworkRequestStatus::isCompleted);
        };

        // Updates the settings if needed, and performs login if we're not already logged,
        // or if the used credentials changed.
        return getUserSettings()
                .compose(RxUtils::pickValue)
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
    public Observable<DataStreamNotification<Beer>> getBeerResultStream(@NonNull final Integer beerId) {
        checkNotNull(beerId);
        Log.v(TAG, "getBeerResultStream");

        final String uri = BeerFetcher.getUniqueUri(beerId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore
                        .getOnceAndStream(requestIdForUri(uri))
                        .compose(RxUtils::pickValue);

        final Observable<Beer> beerObservable = beerStore
                .getOnceAndStream(beerId)
                .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<Beer>> getBeer(@NonNull final Integer beerId) {
        checkNotNull(beerId, "Beer id cannot be null.");
        Log.v(TAG, "getBeer");

        // Trigger a fetch only if full details haven't been fetched
        beerStore.getOnce(beerId)
                .filter(option -> option.match(beer -> !beer.hasDetails(), () -> true))
                .doOnNext(beer -> Log.v(TAG, "Beer not cached, fetching"))
                .subscribe(beer -> fetchBeer(beerId));

        // Does not emit a new notification when only beer metadata changes.
        // This avoids unnecessary view redraws.
        return getBeerResultStream(beerId)
                .distinctUntilChanged();
    }

    private void fetchBeer(@NonNull final Integer beerId) {
        Log.v(TAG, "fetchBeer");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.BEER.toString());
        intent.putExtra("id", beerId);
        context.startService(intent);
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
        beerMetadataStore.getAccessedIds()
                .doOnNext(ids -> Log.d(TAG, "getAccessedBeers: initial of " + ids.size()))
                .doOnNext(subject::onNext)
                .flatMap(ids -> beerMetadataStore.getNewlyAccessedIds(DateTime.now())
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

        return beerListStore.getAllOnce()
                .flatMap(Observable::from)
                .map(ItemList::getKey)
                .filter(search -> !search.startsWith("__"))
                .toList();
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getBeerSearchResultStream(@NonNull final String searchString) {
        checkNotNull(searchString);
        Log.v(TAG, "getBeerSearchResultStream");

        final String queryId = BeerSearchFetcher.getQueryId(RateBeerService.SEARCH, searchString);
        final String uri = BeerSearchFetcher.getUniqueUri(queryId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore
                        .getOnceAndStream(requestIdForUri(uri))
                        .compose(RxUtils::pickValue);

        final Observable<ItemList<String>> beerSearchObservable = beerListStore
                .getOnceAndStream(queryId)
                .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerSearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getBeerSearch(@NonNull final String searchString) {
        checkNotNull(searchString);
        Log.v(TAG, "getBeerSearch");

        // Trigger a fetch only if there was no cached result
        beerListStore.getOnce(BeerSearchFetcher.getQueryId(RateBeerService.SEARCH, searchString))
                .filter(RxUtils::isNoneOrEmpty)
                .doOnNext(results -> Log.v(TAG, "Search not cached, fetching"))
                .subscribe(results -> fetchBeerSearch(searchString));

        return getBeerSearchResultStream(searchString);
    }

    private void fetchBeerSearch(@NonNull final String searchString) {
        checkNotNull(searchString, "Search string cannot be null.");
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

        final String queryId = BeerSearchFetcher.getQueryId(RateBeerService.TOP50);
        final String uri = BeerSearchFetcher.getUniqueUri(queryId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore
                        .getOnceAndStream(requestIdForUri(uri))
                        .compose(RxUtils::pickValue);

        final Observable<ItemList<String>> beerSearchObservable = beerListStore
                .getOnceAndStream(queryId)
                .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerSearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getTopBeers() {
        Log.v(TAG, "getTopBeers");

        // Trigger a fetch only if there was no cached result
        beerListStore.getOnce(BeerSearchFetcher.getQueryId(RateBeerService.TOP50))
                .filter(RxUtils::isNoneOrEmpty)
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

        final String queryId = BeerSearchFetcher.getQueryId(RateBeerService.COUNTRY, countryId);
        final String uri = BeerSearchFetcher.getUniqueUri(queryId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore
                        .getOnceAndStream(requestIdForUri(uri))
                        .compose(RxUtils::pickValue);

        final Observable<ItemList<String>> beerSearchObservable = beerListStore
                .getOnceAndStream(queryId)
                .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerSearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getBeersInCountry(@NonNull final String countryId) {
        Log.v(TAG, "getBeersInCountry");

        // Trigger a fetch only if there was no cached result
        beerListStore.getOnce(BeerSearchFetcher.getQueryId(RateBeerService.COUNTRY, countryId))
                .filter(RxUtils::isNoneOrEmpty)
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

        final String queryId = BeerSearchFetcher.getQueryId(RateBeerService.STYLE, styleId);
        final String uri = BeerSearchFetcher.getUniqueUri(queryId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore
                        .getOnceAndStream(requestIdForUri(uri))
                        .compose(RxUtils::pickValue);

        final Observable<ItemList<String>> beerSearchObservable =
                beerListStore.getOnceAndStream(queryId)
                        .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerSearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getBeersInStyle(@NonNull final String styleId) {
        Log.v(TAG, "getBeersInStyle");

        // Trigger a fetch only if there was no cached result
        beerListStore.getOnce(BeerSearchFetcher.getQueryId(RateBeerService.STYLE, styleId))
                .filter(RxUtils::isNoneOrEmpty)
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
    public Observable<Option<Review>> getReview(final int reviewId) {
        Log.v(TAG, "getReview(" + reviewId + ")");

        // Reviews are never fetched one-by-one, only as a list of reviews. This method can only
        // return reviews from the local store, no fetching.
        return reviewStore.getOnceAndStream(reviewId);
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<Integer>>> getReviewsResultStream(final int beerId) {
        Log.v(TAG, "getReviewsResultStream(" + beerId + ")");

        final String uri = ReviewFetcher.getUniqueUri(beerId);

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore
                        .getOnceAndStream(requestIdForUri(uri))
                        .compose(RxUtils::pickValue);

        final Observable<ItemList<Integer>> reviewListObservable =
                reviewListStore.getOnceAndStream(beerId)
                        .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, reviewListObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<ItemList<Integer>>> getReviews(final int beerId) {
        Log.v(TAG, "getReviews(" + beerId + ")");

        // Trigger a fetch only if there was no cached result
        reviewListStore.getOnce(beerId)
                .filter(RxUtils::isNoneOrEmpty)
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

        return beerStore.getTickedIds()
                .doOnNext(ids -> Log.d(TAG, "Ticked ids: " + ids))
                .map(ItemList::<String>create)
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
    public Observable<DataStreamNotification<Brewer>> getBrewerResultStream(@NonNull final Integer brewerId) {
        Log.v(TAG, "getBrewerResultStream");

        final String uri = BrewerFetcher.getUniqueUri(get(brewerId));

        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore
                        .getOnceAndStream(requestIdForUri(uri))
                        .compose(RxUtils::pickValue);

        final Observable<Brewer> brewerObservable =
                brewerStore.getOnceAndStream(brewerId)
                        .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, brewerObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<Brewer>> getBrewer(@NonNull final Integer brewerId) {
        checkNotNull(brewerId, "Brewer id cannot be null.");
        Log.v(TAG, "getBrewer");

        // Trigger a fetch only if full details haven't been fetched
        brewerStore.getOnceAndStream(brewerId)
                .filter(option -> option.match(brewer -> brewer.name().isEmpty(), () -> true))
                .doOnNext(beer -> Log.v(TAG, "Brewer not cached, fetching"))
                .subscribe(beer -> fetchBrewer(brewerId));

        // Does not emit a new notification when only beer metadata changes.
        // This avoids unnecessary view redraws.
        return getBrewerResultStream(brewerId)
                .distinctUntilChanged();
    }

    private void fetchBrewer(@NonNull final Integer brewerId) {
        Log.v(TAG, "fetchBrewer");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.BREWER.toString());
        intent.putExtra("id", brewerId);
        context.startService(intent);
    }

    //// ACCESS BREWER

    public void accessBrewer(@NonNull final Integer brewerId) {
        checkNotNull(brewerId);
        Log.v(TAG, "accessBrewer(" + brewerId + ")");

        brewerStore.getOnce(brewerId)
                .observeOn(Schedulers.computation())
                .compose(RxUtils::pickValue)
                .map(brewer -> {
                    //brewer.setAccessDate(new Date()); TODO separate access table
                    return brewer;
                })
                .subscribe(brewerStore::put,
                        Log.onError(TAG, "Error updating brewer access date"));
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
        brewerMetadataStore.getAccessedIds()
                .doOnNext(ids -> Log.d(TAG, "getAccessedBrewers: initial of " + ids.size()))
                .doOnNext(subject::onNext)
                .flatMap(ids -> brewerMetadataStore.getNewlyAccessedIds(DateTime.now())
                        .doOnNext(id -> Log.d(TAG, "getAccessedBrewers: accessed " + id))
                        .map(id -> mergeList.call(subject.getValue(), id))
                )
                .subscribe(subject::onNext);

        // Convert the subject to a stream similar to beer searches
        return subject.asObservable()
                .distinctUntilChanged()
                .doOnNext(ids -> Log.d(TAG, "getAccessedBrewers: list now " + ids.size()))
                .map(ids -> new ItemList<String>(null, ids, null))
                .map(DataStreamNotification::onNext);
    }

    public interface Login {
        @NonNull
        Observable<Boolean> call(@NonNull final String username, @NonNull final String password);
    }

    public interface GetUserSettings {
        @NonNull
        Observable<Option<UserSettings>> call();
    }

    public interface SetUserSettings {
        void call(@NonNull final UserSettings userSettings);
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
        Observable<DataStreamNotification<ItemList<String>>> call(@NonNull final String search);
    }

    public interface GetTopBeers {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call();
    }

    public interface GetBeersInCountry {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call(@NonNull final String countryId);
    }

    public interface GetBeersInStyle {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call(@NonNull final String styleId);
    }

    public interface GetReview {
        @NonNull
        Observable<Option<Review>> call(int reviewId);
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
