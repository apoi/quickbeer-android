package quickbeer.android.next.injections;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import quickbeer.android.next.network.utils.NetworkInstrumentation;
import quickbeer.android.next.network.utils.NullNetworkInstrumentation;

/**
 * Created by Pawel Polanski on 4/24/15.
 */
@Module
public class InstrumentationModule {
    @Provides
    @Singleton
    public NetworkInstrumentation<OkHttpClient> providesNetworkInstrumentation() {
        return new NullNetworkInstrumentation();
    }
}
