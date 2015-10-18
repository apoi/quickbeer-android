package quickbeer.android.next.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.fetchers.base.FetcherBase;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.BeerSearch;
import quickbeer.android.next.pojo.NetworkRequestStatus;
import quickbeer.android.next.network.utils.NetworkUtils;
import quickbeer.android.next.utils.Preconditions;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by antti on 17.10.2015.
 */
public class BeerSearchFetcher extends FetcherBase {
    private static final String TAG = BeerSearchFetcher.class.getSimpleName();
    public static final String IDENTIFIER = TAG;

    private final BeerStore beerStore;
    private final BeerSearchStore beerSearchStore;

    public BeerSearchFetcher(@NonNull NetworkApi networkApi,
                             @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                             @NonNull BeerStore beerStore,
                             @NonNull BeerSearchStore beerSearchStore) {
        super(networkApi, updateNetworkRequestStatus);

        Preconditions.checkNotNull(beerStore, "Beer store cannot be null.");
        Preconditions.checkNotNull(beerSearchStore, "Beer search store cannot be null.");

        this.beerStore = beerStore;
        this.beerSearchStore = beerSearchStore;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
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

        final String uri = beerSearchStore.getUriForKey(searchString).toString();
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
    public Uri getContentUri() {
        return beerSearchStore.getContentUri();
    }
}
