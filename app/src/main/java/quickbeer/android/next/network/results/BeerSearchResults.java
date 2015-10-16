package quickbeer.android.next.network.results;

import android.support.annotation.NonNull;

import java.util.List;

import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.utils.Preconditions;

/**
 * Created by antti on 17.10.2015.
 */
public class BeerSearchResults {
    @NonNull
    final private List<Beer> items;

    public BeerSearchResults(@NonNull final List<Beer> items) {
        Preconditions.checkNotNull(items, "Beer list cannot be null.");

        this.items = items;
    }

    @NonNull
    public List<Beer> getItems() {
        return items;
    }
}
