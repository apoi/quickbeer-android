package quickbeer.android.next.data;

import android.support.annotation.NonNull;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;

/**
 * Created by antti on 18.10.2015.
 */
public class DataLayerBase {
    protected final NetworkRequestStatusStore networkRequestStatusStore;
    protected final BeerStore beerStore;
    protected final BeerSearchStore beerSearchStore;
    protected final ReviewStore reviewStore;
    protected final ReviewListStore reviewListStore;

    public DataLayerBase(@NonNull NetworkRequestStatusStore networkRequestStatusStore,
                         @NonNull BeerStore beerStore,
                         @NonNull BeerSearchStore beerSearchStore,
                         @NonNull ReviewStore reviewStore,
                         @NonNull ReviewListStore reviewListStore) {
        Preconditions.checkNotNull(networkRequestStatusStore, "Network request status store cannot be null.");
        Preconditions.checkNotNull(beerStore, "Beer store cannot be null.");
        Preconditions.checkNotNull(beerSearchStore, "Beer search store cannot be null.");
        Preconditions.checkNotNull(reviewStore, "Review store cannot be null.");
        Preconditions.checkNotNull(reviewListStore, "Review list store cannot be null.");

        this.networkRequestStatusStore = networkRequestStatusStore;
        this.beerStore = beerStore;
        this.beerSearchStore = beerSearchStore;
        this.reviewStore = reviewStore;
        this.reviewListStore = reviewListStore;
    }
}
