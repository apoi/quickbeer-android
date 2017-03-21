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
import java.util.Map;

import io.reark.reark.pojo.NetworkRequestStatus;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.NetworkUtils;
import quickbeer.android.utils.StringUtils;
import rx.Observable;
import rx.Single;
import rx.functions.Action1;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BeersInCountryFetcher extends BeerSearchFetcher {

    public BeersInCountryFetcher(@NonNull NetworkApi networkApi,
                                 @NonNull NetworkUtils networkUtils,
                                 @NonNull Action1<NetworkRequestStatus> networkRequestStatus,
                                 @NonNull BeerStore beerStore,
                                 @NonNull BeerListStore beerListStore) {
        super(networkApi, networkUtils, networkRequestStatus, beerStore, beerListStore);
    }

    @Override
    public void fetch(@NonNull Intent intent, int listenerId) {
        checkNotNull(intent);

        if (!intent.hasExtra("countryId")) {
            Timber.e("Missing required fetch parameters!");
            return;
        }

        String countryId = get(intent).getStringExtra("countryId");
        fetchBeerSearch(countryId, listenerId);
    }

    @NonNull
    @Override
    protected List<Beer> sort(@NonNull List<Beer> list) {
        return list;
    }

    @NonNull
    @Override
    protected Single<List<Beer>> createNetworkObservable(@NonNull String countryId) {
        Map<String, String> params = networkUtils.createRequestParams("m", "country");
        params.put("c", countryId);

        return networkApi.getBeersInCountry(params);
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.COUNTRY;
    }
}
