package quickbeer.android.next.data;

import android.support.annotation.NonNull;

import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.utils.Preconditions;

/**
 * Created by antti on 16.10.2015.
 */
public class DataLayerBase {
    protected final NetworkRequestStatusStore networkRequestStatusStore;

    public DataLayerBase(@NonNull NetworkRequestStatusStore networkRequestStatusStore) {
        Preconditions.checkNotNull(networkRequestStatusStore, "Network Request Status Store cannot be null.");

        this.networkRequestStatusStore = networkRequestStatusStore;
    }
}
