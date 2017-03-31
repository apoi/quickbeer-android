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

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.providers.ProgressStatusProvider;
import quickbeer.android.utils.StringUtils;
import rx.Observable;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class BarcodeSearchViewModel extends BeerListViewModel {

    @NonNull
    private final DataLayer.GetBarcodeSearch getBarcodeSearch;

    @NonNull
    private final DataLayer.GetBeerSearch getBeerSearch;

    @NonNull
    private final SearchViewViewModel searchViewViewModel;

    @NonNull
    private String barcode = "";

    @Inject
    BarcodeSearchViewModel(@NonNull DataLayer.GetBeer getBeer,
                           @NonNull DataLayer.GetBeerSearch getBeerSearch,
                           @NonNull DataLayer.GetBarcodeSearch getBarcodeSearch,
                           @NonNull SearchViewViewModel searchViewViewModel,
                           @NonNull ProgressStatusProvider progressStatusProvider) {
        super(getBeer, progressStatusProvider);

        this.getBeerSearch = get(getBeerSearch);
        this.getBarcodeSearch = get(getBarcodeSearch);
        this.searchViewViewModel = get(searchViewViewModel);
    }

    public void setBarcode(@NonNull String barcode) {
        this.barcode = get(barcode);
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

        return getBarcodeSearch.call(barcode)
                .mergeWith(searchObservable);
    }
}
