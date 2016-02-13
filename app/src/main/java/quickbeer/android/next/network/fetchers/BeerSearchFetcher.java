package quickbeer.android.next.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.utils.NetworkUtils;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.BeerSearch;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by antti on 17.10.2015.
 */
public class BeerSearchFetcher extends FetcherBase {
    private static final String TAG = BeerSearchFetcher.class.getSimpleName();

    protected final NetworkApi networkApi;
    private final BeerStore beerStore;
    private final BeerSearchStore beerSearchStore;

    public BeerSearchFetcher(@NonNull NetworkApi networkApi,
                             @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                             @NonNull BeerStore beerStore,
                             @NonNull BeerSearchStore beerSearchStore) {
        super(updateNetworkRequestStatus);

        Preconditions.checkNotNull(networkApi, "Network api cannot be null.");
        Preconditions.checkNotNull(beerStore, "Beer store cannot be null.");
        Preconditions.checkNotNull(beerSearchStore, "Beer search store cannot be null.");

        this.networkApi = networkApi;
        this.beerStore = beerStore;
        this.beerSearchStore = beerSearchStore;
    }

    @Override
    public void fetch(@NonNull Intent intent) {
        final String searchString = intent.getStringExtra("searchString");
        if (searchString != null) {
            fetchBeerSearch(searchString);
        } else {
            Log.e(TAG, "No searchString provided in the intent extras");
        }
    }

    protected void fetchBeerSearch(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string cannot be null.");

        Log.d(TAG, "fetchBeerSearch(" + searchString + ")");
        if (requestMap.containsKey(searchString.hashCode()) &&
                !requestMap.get(searchString.hashCode()).isUnsubscribed()) {
            Log.d(TAG, "Found an ongoing request for search " + searchString);
            return;
        }

        final String uri = beerSearchStore.getUriForId(searchString).toString();
        Subscription subscription = createNetworkObservable(searchString)
                .subscribeOn(Schedulers.computation())
                .map((beers) -> {
                    final List<Integer> beerIds = new ArrayList<>();
                    for (Beer beer : beers) {
                        beerStore.put(beer);
                        beerIds.add(beer.getId());
                    }
                    return new BeerSearch(searchString, beerIds);
                })
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .subscribe(beerSearchStore::put,
                        e -> Log.e(TAG, "Error fetching beer search for '" + searchString + "'", e));

        requestMap.put(searchString.hashCode(), subscription);
        startRequest(uri);
    }

    @NonNull
    protected Observable<List<Beer>> createNetworkObservable(@NonNull final String searchString) {
        Preconditions.checkNotNull(searchString, "Search string cannot be null.");

        return networkApi.search(NetworkUtils.createRequestParams("bn", searchString));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.SEARCH;
    }
}
