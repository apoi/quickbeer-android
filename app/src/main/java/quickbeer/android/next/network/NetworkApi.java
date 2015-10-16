package quickbeer.android.next.network;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import quickbeer.android.next.network.results.BeerSearchResults;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.utils.Preconditions;
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
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .build();

        rateBeerService = restAdapter.create(RateBeerService.class);
    }

    public Observable<List<Beer>> search(Map<String, String> search) {
        return rateBeerService
                .search(search)
                .map(BeerSearchResults::getItems);
    }

    public Observable<List<Beer>> searchTopBeers() {
        return rateBeerService
                .searchTopBeers()
                .map(BeerSearchResults::getItems);
    }
}
