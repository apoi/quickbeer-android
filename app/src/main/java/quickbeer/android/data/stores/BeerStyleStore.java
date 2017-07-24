package quickbeer.android.data.stores;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Collection;

import io.reark.reark.data.stores.DefaultStore;
import ix.Ix;
import polanski.option.Option;
import quickbeer.android.data.pojos.BeerStyle;
import quickbeer.android.data.stores.cores.BeerStyleStoreCore;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.utils.SimpleListSource;
import rx.Observable;

public class BeerStyleStore
        extends DefaultStore<Integer, BeerStyle, Option<BeerStyle>>
        implements SimpleListSource<BeerStyle.SimpleStyle> {

    public BeerStyleStore(@NonNull ResourceProvider resourceProvider, @NonNull Gson gson) {
        super(new BeerStyleStoreCore(resourceProvider, gson),
                BeerStyle::id,
                Option::ofObj,
                Option::none);
    }

    @Override
    public BeerStyle.SimpleStyle getItem(int id) {
        return getOnce(id)
                .toBlocking()
                .value()
                .map(BeerStyle.SimpleStyle::new)
                .orDefault(() -> null);
    }

    @Override
    public Collection<BeerStyle.SimpleStyle> getList() {
        return getOnce()
                .toObservable()
                .flatMap(Observable::from)
                .filter(style -> style.parent() > -1)
                .map(BeerStyle.SimpleStyle::new)
                .toList()
                .toBlocking()
                .first();
    }

    @NonNull
    public Option<BeerStyle.SimpleStyle> getStyle(@NonNull String styleName) {
        return Ix.from(getList())
                .filter(value -> value.getName().equals(styleName))
                .map(Option::ofObj)
                .first(Option.none());
    }
}
