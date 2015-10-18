package quickbeer.android.next.data;

import android.support.annotation.NonNull;

import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.utils.Preconditions;

/**
 * Created by antti on 18.10.2015.
 */
public class DataLayerBase {
    protected final NetworkRequestStatusStore networkRequestStatusStore;
    protected final BeerSearchStore beerSearchStore;
    protected final BeerStore beerStore;

    public DataLayerBase(@NonNull NetworkRequestStatusStore networkRequestStatusStore,
                         @NonNull BeerStore beerStore,
                         @NonNull BeerSearchStore beerSearchStore) {
        Preconditions.checkNotNull(networkRequestStatusStore, "Network request status store cannot be null.");
        Preconditions.checkNotNull(beerStore, "Beer store cannot be null.");
        Preconditions.checkNotNull(beerSearchStore, "Beer search store cannot be null.");

        this.networkRequestStatusStore = networkRequestStatusStore;
        this.beerStore = beerStore;
        this.beerSearchStore = beerSearchStore;
    }
}
