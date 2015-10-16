package quickbeer.android.next.network;

import android.support.annotation.NonNull;

import quickbeer.android.next.utils.Preconditions;
import retrofit.RestAdapter;
import retrofit.client.Client;

/**
 * Created by ttuo on 06/01/15.
 */
public class NetworkApi {

    private final RateBeerService gitHubService;

    public NetworkApi(@NonNull Client client) {
        Preconditions.checkNotNull(client, "Client cannot be null.");

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint("http://localhost")
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .build();
        gitHubService = restAdapter.create(RateBeerService.class);
    }
}
