package quickbeer.android.next.network;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;

import io.reark.reark.network.fetchers.Fetcher;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.DataLayerBase;
import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;

/**
 * Created by ttuo on 16/04/15.
 */
public class ServiceDataLayer extends DataLayerBase {
    private static final String TAG = ServiceDataLayer.class.getSimpleName();

    @NonNull
    final private Collection<Fetcher> fetchers;

    public ServiceDataLayer(@NonNull Fetcher beerSearchFetcher,
                            @NonNull Fetcher topBeersFetcher,
                            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
                            @NonNull BeerStore beerStore,
                            @NonNull BeerSearchStore beerSearchStore) {
        super(networkRequestStatusStore, beerStore, beerSearchStore);

        fetchers = Arrays.asList(
                beerSearchFetcher,
                topBeersFetcher
        );
    }

    public void processIntent(@NonNull Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null.");

        final String serviceUriString = intent.getStringExtra("serviceUriString");
        if (serviceUriString != null) {
            final Uri serviceUri = Uri.parse(serviceUriString);
            Fetcher matchingFetcher = findFetcher(serviceUri);
            if (matchingFetcher != null) {
                Log.v(TAG, "Fetcher found for " + serviceUri);
                matchingFetcher.fetch(intent);
            } else {
                Log.e(TAG, "Unknown Uri " + serviceUri);
            }
        } else {
            Log.e(TAG, "No Uri defined");
        }
    }

    @Nullable
    private Fetcher findFetcher(@NonNull Uri serviceUri) {
        Preconditions.checkNotNull(serviceUri, "Service URI cannot be null.");

        for (Fetcher fetcher : fetchers) {
            if (fetcher.getServiceUri().equals(serviceUri)) {
                return fetcher;
            }
        }
        return null;
    }
}
