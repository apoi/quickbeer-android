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
package quickbeer.android.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import io.reark.reark.pojo.NetworkRequestStatus;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.NetworkUtils;
import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

public class BarcodeSearchFetcher extends BeerSearchFetcher {

    public BarcodeSearchFetcher(@NonNull final NetworkApi networkApi,
                                @NonNull final NetworkUtils networkUtils,
                                @NonNull final Action1<NetworkRequestStatus> requestStatus,
                                @NonNull final BeerStore beerStore,
                                @NonNull final BeerListStore beerListStore) {
        super(networkApi, networkUtils, requestStatus, beerStore, beerListStore);
    }

    @Override
    public void fetch(@NonNull final Intent intent) {
        final String barcode = intent.getStringExtra("barcode");

        if (barcode != null) {
            fetchBeerSearch(barcode);
        } else {
            Timber.e("No barcode provided in the intent extras");
        }
    }

    @NonNull
    @Override
    protected Observable<List<Beer>> createNetworkObservable(@NonNull final String barcode) {
        return networkApi.barcode(networkUtils.createRequestParams("upc", barcode));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.BARCODE;
    }
}