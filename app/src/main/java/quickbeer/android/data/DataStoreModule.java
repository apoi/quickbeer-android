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
package quickbeer.android.data;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reark.reark.network.fetchers.UriFetcherManager;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.data.stores.BrewerListStore;
import quickbeer.android.data.stores.BrewerMetadataStore;
import quickbeer.android.data.stores.BrewerStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.data.stores.ReviewStore;
import quickbeer.android.data.stores.StoreModule;
import quickbeer.android.data.stores.UserStore;
import quickbeer.android.injections.ForApplication;
import quickbeer.android.network.fetchers.FetcherModule;

@Module(includes = { FetcherModule.class, StoreModule.class })
public final class DataStoreModule {

    @Provides
    static DataLayer.Login provideLogin(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::login;
    }

    @Provides
    static DataLayer.GetUser provideGetUser(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getUser;
    }

    @Provides
    static DataLayer.SetUser provideSetUser(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::setUser;
    }

    @Provides
    static DataLayer.GetBeer provideGetBeer(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getBeer;
    }

    @Provides
    static DataLayer.AccessBeer provideAccessBeer(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::accessBeer;
    }

    @Provides
    static DataLayer.GetAccessedBeers provideGetAccessedBeers(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getAccessedBeers;
    }

    @Provides
    static DataLayer.GetBrewer provideGetBrewer(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getBrewer;
    }

    @Provides
    static DataLayer.AccessBrewer provideAccessBrewer(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::accessBrewer;
    }

    @Provides
    static DataLayer.GetAccessedBrewers provideGetAccessedBrewers(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getAccessedBrewers;
    }

    @Provides
    static DataLayer.GetBeerSearchQueries provideGetBeerSearchQueries(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getBeerSearchQueriesOnce;
    }

    @Provides
    static DataLayer.GetBeerSearch provideGetBeerSearch(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getBeerSearch;
    }

    @Provides
    static DataLayer.GetBarcodeSearch provideGetBarcodeSearch(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getBarcodeSearch;
    }

    @Provides
    static DataLayer.GetTopBeers provideGetTopBeers(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getTopBeers;
    }

    @Provides
    static DataLayer.GetBeersInCountry provideGetBeersInCountry(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getBeersInCountry;
    }

    @Provides
    static DataLayer.GetBeersInStyle provideGetBeersInStyle(

            @NonNull final DataLayer dataLayer) {
        return dataLayer::getBeersInStyle;
    }

    @Provides
    static DataLayer.GetReview provideGetReview(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getReview;
    }

    @Provides
    static DataLayer.GetReviews provideGetReviews(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getReviews;
    }

    @Provides
    static DataLayer.GetTickedBeers provideGetTickedBeers(
            @NonNull final DataLayer dataLayer) {
        return dataLayer::getTickedBeers;
    }

    @Provides
    @Singleton
    static DataLayer provideApplicationDataLayer(
            @ForApplication @NonNull final Context context,
            @NonNull final UserStore userStore,
            @NonNull final NetworkRequestStatusStore requestStatusStore,
            @NonNull final BeerStore beerStore,
            @NonNull final BeerListStore beerListStore,
            @NonNull final BeerMetadataStore beerMetadataStore,
            @NonNull final ReviewStore reviewStore,
            @NonNull final ReviewListStore reviewListStore,
            @NonNull final BrewerStore brewerStore,
            @NonNull final BrewerListStore brewerListStore,
            @NonNull final BrewerMetadataStore brewerMetadataStore) {
        return new DataLayer(context,
                userStore,
                requestStatusStore,
                beerStore,
                beerListStore,
                beerMetadataStore,
                reviewStore,
                reviewListStore,
                brewerStore,
                brewerListStore,
                brewerMetadataStore);
    }

    @Provides
    @Singleton
    static ServiceDataLayer provideServiceDataLayer(
            @NonNull final UriFetcherManager fetcherManager,
            @NonNull final NetworkRequestStatusStore requestStatusStore,
            @NonNull final BeerStore beerStore,
            @NonNull final BeerListStore beerListStore,
            @NonNull final BeerMetadataStore beerMetadataStore,
            @NonNull final ReviewStore reviewStore,
            @NonNull final ReviewListStore reviewListStore,
            @NonNull final BrewerStore brewerStore,
            @NonNull final BrewerListStore brewerListStore,
            @NonNull final BrewerMetadataStore brewerMetadataStore) {
        return new ServiceDataLayer(fetcherManager,
                requestStatusStore,
                beerStore,
                beerListStore,
                beerMetadataStore,
                reviewStore,
                reviewListStore,
                brewerStore,
                brewerListStore,
                brewerMetadataStore);
    }
}
