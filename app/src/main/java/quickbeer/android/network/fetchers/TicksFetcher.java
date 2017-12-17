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

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reark.reark.pojo.NetworkRequestStatus;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.data.stores.UserStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.fetchers.actions.LoginAndRetry;
import quickbeer.android.network.utils.NetworkUtils;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class TicksFetcher extends BeerSearchFetcher {

    @NonNull
    private final UserStore userStore;

    public TicksFetcher(@NonNull NetworkApi networkApi,
                        @NonNull NetworkUtils networkUtils,
                        @NonNull Consumer<NetworkRequestStatus> networkRequestStatus,
                        @NonNull BeerStore beerStore,
                        @NonNull BeerListStore beerListStore,
                        @NonNull UserStore userStore) {
        super(networkApi, networkUtils, networkRequestStatus, beerStore, beerListStore);

        this.userStore = get(userStore);
    }

    @Override
    public void fetch(@NonNull Intent intent, int listenerId) {
        checkNotNull(intent);

        if (!intent.hasExtra("userId")) {
            Timber.e("Missing required fetch parameters!");
            return;
        }

        String userId = get(intent).getStringExtra("userId");
        fetchBeerSearch(userId, listenerId);
    }

    @Override
    @NonNull
    protected List<Beer> sort(@NonNull List<Beer> list) {
        return sortByTickDate(list);
    }

    @NonNull
    @Override
    protected Single<List<Beer>> createNetworkObservable(@NonNull String userId) {
        Map<String, String> params = networkUtils.createRequestParams("m", "1");
        params.put("u", userId);

        return networkApi.getTicks(params)
                .retryWhen(new LoginAndRetry(networkApi, userStore));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.USER_TICKS;
    }
}
