/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.viewmodels;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Named;

import io.reark.reark.data.DataStreamNotification;
import polanski.option.Option;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.BeerStyle;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.BeerStyleStore;
import quickbeer.android.providers.ProgressStatusProvider;
import quickbeer.android.rx.RxUtils;
import quickbeer.android.utils.StringUtils;
import rx.Observable;
import rx.Single;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class BeersInStyleViewModel extends BeerListViewModel {

    @NonNull
    private final DataLayer.GetBeersInStyle getBeersInStyle;

    @NonNull
    private final DataLayer.GetBeerSearch getBeerSearch;

    @NonNull
    private final BeerStyleStore beerStyleStore;

    @NonNull
    private final SearchViewViewModel searchViewViewModel;

    @NonNull
    private final Integer styleId;

    @Inject
    BeersInStyleViewModel(@Named("id") Integer styleId,
                          @NonNull DataLayer.GetBeer getBeer,
                          @NonNull DataLayer.GetBeerSearch getBeerSearch,
                          @NonNull DataLayer.GetBeersInStyle getBeersInStyle,
                          @NonNull BeerStyleStore beerStyleStore,
                          @NonNull SearchViewViewModel searchViewViewModel,
                          @NonNull ProgressStatusProvider progressStatusProvider) {
        super(getBeer, progressStatusProvider);

        this.styleId = get(styleId);
        this.getBeerSearch = get(getBeerSearch);
        this.getBeersInStyle = get(getBeersInStyle);
        this.beerStyleStore = get(beerStyleStore);
        this.searchViewViewModel = get(searchViewViewModel);
    }

    @NonNull
    public Single<Option<String>> styleName() {
        return beerStyleStore.getOnce(styleId)
                .map(option -> option.map(BeerStyle::name));
    }

    @NonNull
    public Single<Option<String>> styleDescription() {
        return beerStyleStore.getOnce(styleId)
                .map(option -> option.map(BeerStyle::description));
    }

    @NonNull
    @Override
    protected Observable<DataStreamNotification<ItemList<String>>> sourceObservable() {
        Observable<DataStreamNotification<ItemList<String>>> searchObservable =
                get(searchViewViewModel)
                        .getQueryStream()
                        .distinctUntilChanged()
                        .filter(StringUtils::hasValue)
                        .doOnNext(query -> Timber.d("query(%s)", query))
                        .switchMap(query -> get(getBeerSearch).call(query));

        return getBeersInStyle.call(String.valueOf(styleId))
                .mergeWith(searchObservable);
    }
}
