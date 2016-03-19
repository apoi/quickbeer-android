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
package quickbeer.android.next.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.utils.NetworkUtils;
import quickbeer.android.next.pojo.Beer;
import rx.Observable;
import rx.functions.Action1;

public class TopInCountryFetcher extends BeerSearchFetcher {
    private static final String TAG = TopInCountryFetcher.class.getSimpleName();

    public TopInCountryFetcher(@NonNull NetworkApi networkApi,
                               @NonNull NetworkUtils networkUtils,
                               @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                               @NonNull BeerStore beerStore,
                               @NonNull BeerSearchStore beerSearchStore) {
        super(networkApi, networkUtils, updateNetworkRequestStatus, beerStore, beerSearchStore);
    }

    @Override
    public void fetch(@NonNull Intent intent) {
        final String countryId = intent.getStringExtra("countryId");
        if (countryId != null) {
            fetchBeerSearch(countryId);
        } else {
            Log.e(TAG, "No countryId provided in the intent extras");
        }
    }

    @NonNull
    @Override
    protected Observable<List<Beer>> createNetworkObservable(String countryId) {
        Map<String, String> params = networkUtils.createRequestParams("m", "country");
        params.put("c", countryId);

        return networkApi.searchTopBeers(params);
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.COUNTRY;
    }
}