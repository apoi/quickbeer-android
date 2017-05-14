package quickbeer.android.data.stores;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import io.reark.reark.data.stores.DefaultStore;
import polanski.option.Option;
import quickbeer.android.data.pojos.BeerStyle;
import quickbeer.android.data.stores.cores.BeerStyleStoreCore;
import quickbeer.android.providers.ResourceProvider;

public class BeerStyleStore extends DefaultStore<Integer, BeerStyle, Option<BeerStyle>> {

    public BeerStyleStore(@NonNull ResourceProvider resourceProvider, @NonNull Gson gson) {
        super(new BeerStyleStoreCore(resourceProvider, gson),
                BeerStyle::id,
                Option::ofObj,
                Option::none);
    }
}
