package quickbeer.android.next.network.fetchers;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.List;

import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.NetworkRequestStatus;
import quickbeer.android.next.network.utils.NetworkUtils;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by antti on 17.10.2015.
 */
public class TopBeersFetcher extends BeerSearchFetcher {
    private static final String TAG = TopBeersFetcher.class.getSimpleName();
    public static final String SEARCH = "//top50";

    public TopBeersFetcher(@NonNull NetworkApi networkApi,
                           @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                           @NonNull BeerStore beerStore,
                           @NonNull BeerSearchStore beerSearchStore) {
        super(networkApi, updateNetworkRequestStatus, beerStore, beerSearchStore);
    }

    @Override
    public void fetch(@NonNull Intent intent) {
        fetchBeerSearch(SEARCH);
    }

    @NonNull
    @Override
    protected Observable<List<Beer>> createNetworkObservable(String searchString) {
        return networkApi.searchTopBeers(NetworkUtils.createRequestParams("m", "top50"));
    }
}
