package quickbeer.android.next.data.store;

import android.content.ContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class StoreModule {
    @Provides
    @Singleton
    public UserSettingsStore provideUserSettingsStore(ContentResolver contentResolver) {
        return new UserSettingsStore(contentResolver);
    }

    @Provides
    @Singleton
    public NetworkRequestStatusStore provideNetworkRequestStatusStore(ContentResolver contentResolver) {
        return new NetworkRequestStatusStore(contentResolver);
    }

    @Provides
    @Singleton
    public BeerStore provideBeerStore(ContentResolver contentResolver) {
        return new BeerStore(contentResolver);
    }

    @Provides
    @Singleton
    public BeerSearchStore provideBeerSearchStore(ContentResolver contentResolver) {
        return new BeerSearchStore(contentResolver);
    }
}
