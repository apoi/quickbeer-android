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
import quickbeer.android.next.data.store.UserSettingsStore;
import quickbeer.android.next.network.NetworkService;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.fetchers.TopBeersFetcher;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.BeerSearch;
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
                     @NonNull BeerSearchStore beerSearchStore) {
        super(networkRequestStatusStore, beerStore, beerSearchStore);

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

        final Uri uri = beerSearchStore.getUriForKey(searchString);

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

        final Uri uri = beerSearchStore.getUriForKey(TopBeersFetcher.SEARCH);

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
}
