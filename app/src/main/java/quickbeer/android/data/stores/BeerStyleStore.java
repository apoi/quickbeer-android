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
        implements SimpleListSource<BeerStyle> {

    public BeerStyleStore(@NonNull ResourceProvider resourceProvider, @NonNull Gson gson) {
        super(new BeerStyleStoreCore(resourceProvider, gson),
                BeerStyle::getId,
                Option::ofObj,
                Option::none);
    }

    @Override
    public BeerStyle getItem(int id) {
        return getOnce(id)
                .toBlocking()
                .value()
                .orDefault(() -> null);
    }

    @Override
    public Collection<BeerStyle> getList() {
        return getOnce()
                .toObservable()
                .flatMap(Observable::from)
                .filter(style -> style.getParent() > -1)
                .toList()
                .toBlocking()
                .first();
    }

    @NonNull
    public Option<BeerStyle> getStyle(@NonNull String styleName) {
        return Ix.from(getList())
                .filter(value -> styleName.equals(value.getName()))
                .map(Option::ofObj)
                .first(Option.none());
    }
}
