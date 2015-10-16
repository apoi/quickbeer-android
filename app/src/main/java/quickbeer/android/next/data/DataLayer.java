package quickbeer.android.next.data;

import android.content.Context;
import android.support.annotation.NonNull;

import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.data.store.UserSettingsStore;
import quickbeer.android.next.pojo.UserSettings;
import quickbeer.android.next.utils.Preconditions;
import rx.Observable;

public class DataLayer extends DataLayerBase {
    private static final String TAG = DataLayer.class.getSimpleName();

    private final Context context;
    protected final UserSettingsStore userSettingsStore;
    public static final int DEFAULT_USER_ID = 0;

    public DataLayer(@NonNull Context context,
                     @NonNull UserSettingsStore userSettingsStore,
                     @NonNull NetworkRequestStatusStore networkRequestStatusStore) {
        super(networkRequestStatusStore);

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
}
