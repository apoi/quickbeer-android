package quickbeer.android.next.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import io.reark.reark.pojo.NetworkRequestStatus;
import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.utils.NetworkUtils;
import quickbeer.android.next.pojo.Beer;
import rx.Observable;
import rx.functions.Action1;

public class TopBeersFetcher extends BeerSearchFetcher {
    private static final String TAG = TopBeersFetcher.class.getSimpleName();
    public static final String SEARCH = "__top50";

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

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.TOP50;
    }
}
