package quickbeer.android.next.network;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.pojo.Beer;
import retrofit.RestAdapter;
import retrofit.client.Client;
import rx.Observable;

/**
 * Created by antti on 17.10.2015.
 */
public class NetworkApi {

    private final RateBeerService rateBeerService;

    public NetworkApi(@NonNull Client client) {
        Preconditions.checkNotNull(client, "Client cannot be null.");

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint("http://www.ratebeer.com")
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();

        rateBeerService = restAdapter.create(RateBeerService.class);
    }

    public Observable<Beer> getBeer(Map<String, String> params) {
        return rateBeerService
                .getBeer(params)
                .map(list -> list.get(0)); // API returns a list of one beer
    }

    public Observable<List<Beer>> search(Map<String, String> params) {
        return rateBeerService
                .search(params);
    }

    public Observable<List<Beer>> searchTopBeers(Map<String, String> params) {
        return rateBeerService
                .searchTopBeers(params);
    }
}
