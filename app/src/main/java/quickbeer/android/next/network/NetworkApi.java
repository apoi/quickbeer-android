package quickbeer.android.next.network;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.Review;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.converter.GsonConverter;
import rx.Observable;

public class NetworkApi {

    private final RateBeerService rateBeerService;

    public NetworkApi(@NonNull Client client, @NonNull Gson gson) {
        Preconditions.checkNotNull(client, "Client cannot be null.");
        Preconditions.checkNotNull(client, "Gson cannot be null.");

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint("http://www.ratebeer.com")
                .setConverter(new GsonConverter(gson))
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

    public Observable<List<Review>> getReviews(Map<String, String> params) {
        return rateBeerService
                .getReviews(params);
    }
}
