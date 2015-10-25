package quickbeer.android.next.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.fetchers.base.FetcherBase;
import quickbeer.android.next.network.utils.NetworkUtils;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.NetworkRequestStatus;
import quickbeer.android.next.utils.Preconditions;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by antti on 25.10.2015.
 */
public class BeerFetcher extends FetcherBase {
    private static final String TAG = BeerFetcher.class.getSimpleName();
    public static final String IDENTIFIER = TAG;

    private final BeerStore beerStore;

    public BeerFetcher(@NonNull NetworkApi networkApi,
                       @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                       @NonNull BeerStore beerStore) {
        super(networkApi, updateNetworkRequestStatus);

        Preconditions.checkNotNull(beerStore, "Beer store cannot be null.");

        this.beerStore = beerStore;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public void fetch(Intent intent) {
        Preconditions.checkNotNull(intent, "Fetch intent cannot be null.");

        final int beerId = intent.getIntExtra("id", -1);
        if (beerId != -1) {
            fetchBeer(beerId);
        } else {
            Log.e(TAG, "No beerId provided in the intent extras");
        }
    }

    private void fetchBeer(final int beerId) {
        Log.d(TAG, "fetchBeer(" + beerId + ")");

        if (requestMap.containsKey(beerId) && !requestMap.get(beerId).isUnsubscribed()) {
            Log.d(TAG, "Found an ongoing request for beer " + beerId);
            return;
        }

        final String uri = beerStore.getUriForKey(beerId).toString();
        Subscription subscription = createNetworkObservable(beerId)
                .subscribeOn(Schedulers.computation())
                .doOnError(doOnError(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .subscribe(beerStore::put,
                        e -> Log.e(TAG, "Error fetching beer " + beerId, e));

        //requestMap.put(beerId, subscription);
        //startRequest(uri);
    }

    @NonNull
    private Observable<Beer> createNetworkObservable(int beerId) {
        return networkApi.getBeer(NetworkUtils.createRequestParams("bd", String.valueOf(beerId)));
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return beerStore.getContentUri();
    }
}
