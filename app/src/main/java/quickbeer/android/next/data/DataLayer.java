package quickbeer.android.next.data;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.data.store.UserSettingsStore;
import quickbeer.android.next.data.utils.DataLayerUtils;
import quickbeer.android.next.network.fetchers.TopBeersFetcher;
import quickbeer.android.next.pojo.BeerSearch;
import quickbeer.android.next.pojo.NetworkRequestStatus;
import quickbeer.android.next.pojo.UserSettings;
import quickbeer.android.next.utils.Preconditions;
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

    public interface GetUserSettings {
        @NonNull
        Observable<UserSettings> call();
    }

    public interface SetUserSettings {
        void call(@NonNull UserSettings userSettings);
    }

    @NonNull
    public Observable<DataStreamNotification<BeerSearch>> getBeerSeach(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string cannot be null.");

        final Uri uri = beerSearchStore.getUriForKey(searchString);
        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());
        final Observable<BeerSearch> beerSearchObservable =
                beerSearchStore.getStream(searchString);
        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerSearchObservable);
    }

    @NonNull
    public Observable<DataStreamNotification<BeerSearch>> getTopBeers() {
        final Uri uri = beerSearchStore.getUriForKey(TopBeersFetcher.SEARCH);
        final Observable<NetworkRequestStatus> networkRequestStatusObservable =
                networkRequestStatusStore.getStream(uri.toString().hashCode());
        final Observable<BeerSearch> beerSearchObservable =
                beerSearchStore.getStream(TopBeersFetcher.SEARCH);
        return DataLayerUtils.createDataStreamNotificationObservable(
                networkRequestStatusObservable, beerSearchObservable);
    }
}
