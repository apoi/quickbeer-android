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
package quickbeer.android.next.data;

import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reark.reark.network.fetchers.Fetcher;
import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;
import quickbeer.android.next.data.store.StoreModule;
import quickbeer.android.next.data.store.UserSettingsStore;
import quickbeer.android.next.injections.ForApplication;
import quickbeer.android.next.network.ServiceDataLayer;
import quickbeer.android.next.network.fetchers.FetcherModule;

@Module(includes = { FetcherModule.class, StoreModule.class })
public final class DataStoreModule {

    @Provides
    public DataLayer.GetUserSettings provideGetUserSettings(DataLayer dataLayer) {
        return dataLayer::getUserSettings;
    }

    @Provides
    public DataLayer.SetUserSettings provideSetUserSettings(DataLayer dataLayer) {
        return dataLayer::setUserSettings;
    }

    @Provides
    public DataLayer.GetBeer provideGetBeer(DataLayer dataLayer) {
        return dataLayer::getBeer;
    }

    @Provides
    public DataLayer.AccessBeer provideAccessBeer(DataLayer dataLayer) {
        return dataLayer::accessBeer;
    }

    @Provides
    public DataLayer.GetAccessedBeers provideGetAccessedBeers(DataLayer dataLayer) {
        return dataLayer::getAccessedBeers;
    }

    @Provides
    public DataLayer.GetBeerSearchQueries provideGetBeerSearchQueries(DataLayer dataLayer) {
        return dataLayer::getBeerSearchQueries;
    }

    @Provides
    public DataLayer.GetBeerSearch provideGetBeerSearch(DataLayer dataLayer) {
        return dataLayer::getBeerSearch;
    }

    @Provides
    public DataLayer.GetTopBeers provideGetTopBeers(DataLayer dataLayer) {
        return dataLayer::getTopBeers;
    }

    @Provides
    public DataLayer.GetBeersInCountry provideGetBeersInCountry(DataLayer dataLayer) {
        return dataLayer::getBeersInCountry;
    }

    @Provides
    public DataLayer.GetBeersInStyle provideGetBeersInStyle(DataLayer dataLayer) {
        return dataLayer::getBeersInStyle;
    }

    @Provides
    public DataLayer.GetReview provideGetReview(DataLayer dataLayer) {
        return dataLayer::getReview;
    }

    @Provides
    public DataLayer.GetReviews provideGetReviews(DataLayer dataLayer) {
        return dataLayer::getReviews;
    }

    @Provides
    @Singleton
    public DataLayer provideApplicationDataLayer(@ForApplication Context context,
                                                 UserSettingsStore userSettingsStore,
                                                 NetworkRequestStatusStore networkRequestStatusStore,
                                                 BeerStore beerStore,
                                                 BeerSearchStore beerSearchStore,
                                                 ReviewStore reviewStore,
                                                 ReviewListStore reviewListStore) {
        return new DataLayer(context,
                userSettingsStore,
                networkRequestStatusStore,
                beerStore,
                beerSearchStore,
                reviewStore,
                reviewListStore);
    }

    @Provides
    @Singleton
    public ServiceDataLayer provideServiceDataLayer(@Named("beerFetcher") Fetcher beerFetcher,
                                                    @Named("beerSearchFetcher") Fetcher beerSearchFetcher,
                                                    @Named("topBeersFetcher") Fetcher topBeersFetcher,
                                                    @Named("beersInCountryFetcher") Fetcher beersInCountryFetcher,
                                                    @Named("beersInStyleFetcher") Fetcher beersInStyleFetcher,
                                                    @Named("reviewFetcher") Fetcher reviewFetcher,
                                                    NetworkRequestStatusStore networkRequestStatusStore,
                                                    BeerStore beerStore,
                                                    BeerSearchStore beerSearchStore,
                                                    ReviewStore reviewStore,
                                                    ReviewListStore reviewListStore) {
        return new ServiceDataLayer(beerFetcher,
                beerSearchFetcher,
                topBeersFetcher,
                beersInCountryFetcher,
                beersInStyleFetcher,
                reviewFetcher,
                networkRequestStatusStore,
                beerStore,
                beerSearchStore,
                reviewStore,
                reviewListStore);
    }
}
