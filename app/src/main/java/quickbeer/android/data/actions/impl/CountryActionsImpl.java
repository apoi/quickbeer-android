/*
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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

package quickbeer.android.data.actions.impl;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.pojo.NetworkRequestStatus;
import polanski.option.Option;
import quickbeer.android.data.actions.CountryActions;
import quickbeer.android.data.pojos.Country;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.CountryStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.network.NetworkService;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.fetchers.BeerSearchFetcher;
import quickbeer.android.rx.RxUtils;
import rx.Observable;
import rx.Single;
import timber.log.Timber;

import static quickbeer.android.data.stores.NetworkRequestStatusStore.requestIdForUri;

public class CountryActionsImpl extends ApplicationDataLayer implements CountryActions {

    @NonNull
    private final NetworkRequestStatusStore requestStatusStore;

    @NonNull
    private final BeerListStore beerListStore;

    @NonNull
    private final CountryStore countryStore;

    @Inject
    public CountryActionsImpl(@NonNull Context context,
                              @NonNull NetworkRequestStatusStore requestStatusStore,
                              @NonNull BeerListStore beerListStore,
                              @NonNull CountryStore countryStore) {
        super(context);

        this.requestStatusStore = requestStatusStore;
        this.beerListStore = beerListStore;
        this.countryStore = countryStore;
    }

    //// COUNTRIES

    @Override
    @NonNull
    public Single<Option<Country>> get(int countryId) {
        Timber.v("getCountry(%s)", countryId);

        return countryStore.getOnce(countryId);
    }

    //// BEERS IN COUNTRY

    @Override
    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> beers(int countryId) {
        Timber.v("getBeersInCountry(%s)", countryId);

        // Trigger a fetch only if there was no cached result
        Observable<Option<ItemList<String>>> triggerFetchIfEmpty =
                beerListStore.getOnce(BeerSearchFetcher.getQueryId(RateBeerService.COUNTRY, String.valueOf(countryId)))
                        .toObservable()
                        .filter(RxUtils::isNoneOrEmpty)
                        .doOnNext(__ -> {
                            Timber.v("Search not cached, fetching");
                            fetchBeersInCountry(countryId);
                        });

        return getBeersInCountryResultStream(countryId)
                .mergeWith(triggerFetchIfEmpty.flatMap(__ -> Observable.empty()));
    }

    @NonNull
    private Observable<DataStreamNotification<ItemList<String>>> getBeersInCountryResultStream(int countryId) {
        Timber.v("getBeersInCountryResultStream(%s)", countryId);

        String queryId = BeerSearchFetcher.getQueryId(RateBeerService.COUNTRY, String.valueOf(countryId));
        String uri = BeerSearchFetcher.getUniqueUri(queryId);

        Observable<NetworkRequestStatus> requestStatusObservable =
                requestStatusStore.getOnceAndStream(requestIdForUri(uri))
                        .compose(RxUtils::pickValue); // No need to filter stale statuses?

        Observable<ItemList<String>> beerSearchObservable =
                beerListStore.getOnceAndStream(queryId)
                        .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                requestStatusObservable, beerSearchObservable);
    }

    private int fetchBeersInCountry(int countryId) {
        Timber.v("fetchBeersInCountry");

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.COUNTRY.toString());
        intent.putExtra("listenerId", listenerId);
        intent.putExtra("countryId", countryId);
        getContext().startService(intent);

        return listenerId;
    }
}
