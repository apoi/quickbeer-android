package quickbeer.android.next.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import quickbeer.android.next.network.utils.NetworkInstrumentation;
import quickbeer.android.next.network.utils.StringDeserializer;
import retrofit.client.Client;
import retrofit.client.OkClient;

/**
 * Created by Pawel Polanski on 5/16/15.
 */
@Module
public final class NetworkModule {
    @Provides
    @Singleton
    public NetworkApi provideNetworkApi(@Named("okClient") Client client) {
        return new NetworkApi(client);
    }

    @Provides
    @Singleton
    @Named("okClient")
    public Client provideOkClient(OkHttpClient okHttpClient) {
        return new OkClient(okHttpClient);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(NetworkInstrumentation<OkHttpClient> networkInstrumentation) {
        return networkInstrumentation.decorateNetwork(new OkHttpClient());
    }

    @Provides
    @Singleton
    public Gson unescapingGson() {
        return new GsonBuilder().registerTypeAdapter(String.class, new StringDeserializer()).create();
    }
}
