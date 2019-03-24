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

import android.net.Uri;
import androidx.annotation.NonNull;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reark.reark.network.fetchers.Fetcher;
import io.reark.reark.network.fetchers.UriFetcherManager;
import quickbeer.android.Constants.Fetchers;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.data.stores.BrewerMetadataStore;
import quickbeer.android.data.stores.BrewerStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.data.stores.ReviewStore;
import quickbeer.android.data.stores.UserStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.NetworkModule;
import quickbeer.android.network.fetchers.impl.BarcodeSearchFetcher;
import quickbeer.android.network.fetchers.impl.BeerFetcher;
import quickbeer.android.network.fetchers.impl.BeerSearchFetcher;
import quickbeer.android.network.fetchers.impl.BeersInCountryFetcher;
import quickbeer.android.network.fetchers.impl.BeersInStyleFetcher;
import quickbeer.android.network.fetchers.impl.BrewerBeersFetcher;
import quickbeer.android.network.fetchers.impl.BrewerFetcher;
import quickbeer.android.network.fetchers.impl.LoginFetcher;
import quickbeer.android.network.fetchers.impl.ReviewFetcher;
import quickbeer.android.network.fetchers.impl.ReviewsFetcher;
import quickbeer.android.network.fetchers.impl.TickBeerFetcher;
import quickbeer.android.network.fetchers.impl.TicksFetcher;
import quickbeer.android.network.fetchers.impl.TopBeersFetcher;
import quickbeer.android.network.utils.NetworkUtils;

@Module(includes = NetworkModule.class)
public final class FetcherModule {

    @Provides
    @Named(Fetchers.LOGIN)
    static Fetcher<String> provideLoginFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull ClearableCookieJar cookieJar,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            @NonNull UserStore userStore) {
        return new LoginFetcher(networkApi,
                cookieJar,
                requestStatusStore::put,
                userStore);
    }

    @Provides
    @Named(Fetchers.BEER)
    static Fetcher<String> provideBeerFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull NetworkUtils networkUtils,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            @NonNull BeerStore beerStore,
            @NonNull BeerMetadataStore metadataStore) {
        return new BeerFetcher(networkApi,
                networkUtils,
                requestStatusStore::put,
                beerStore,
                metadataStore);
    }

    @Provides
    @Named(Fetchers.BEER_SEARCH)
    static Fetcher<String> provideBeerSearchFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull NetworkUtils networkUtils,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            @NonNull BeerStore beerStore,
            @NonNull BeerListStore beerListStore) {
        return new BeerSearchFetcher(networkApi,
                networkUtils,
                requestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named(Fetchers.BARCODE_SEARCH)
    static Fetcher<String> provideBarcodeSearchFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull NetworkUtils networkUtils,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            @NonNull BeerStore beerStore,
            @NonNull BeerListStore beerListStore) {
        return new BarcodeSearchFetcher(networkApi,
                networkUtils,
                requestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named(Fetchers.TOP_BEERS)
    static Fetcher<String> provideTopBeersFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull NetworkUtils networkUtils,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            @NonNull BeerStore beerStore,
            @NonNull BeerListStore beerListStore) {
        return new TopBeersFetcher(networkApi,
                networkUtils,
                requestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named(Fetchers.BEERS_IN_COUNTRY)
    static Fetcher<String> provideBeersInCountryFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull NetworkUtils networkUtils,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            @NonNull BeerStore beerStore,
            @NonNull BeerListStore beerListStore) {
        return new BeersInCountryFetcher(networkApi,
                networkUtils,
                requestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named(Fetchers.BEERS_IN_STYLE)
    static Fetcher<String> provideBeersInStyleFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull NetworkUtils networkUtils,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            @NonNull BeerStore beerStore,
            @NonNull BeerListStore beerListStore) {
        return new BeersInStyleFetcher(networkApi,
                networkUtils,
                requestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named(Fetchers.BREWER)
    static Fetcher<String> provideBrewerFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull NetworkUtils networkUtils,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            @NonNull BrewerStore brewerStore,
            @NonNull BrewerMetadataStore metadataStore) {
        return new BrewerFetcher(networkApi,
                networkUtils,
                requestStatusStore::put,
                brewerStore,
                metadataStore);
    }

    @Provides
    @Named(Fetchers.BREWER_BEERS)
    static Fetcher<String> provideBrewerBeersFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull NetworkUtils networkUtils,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            @NonNull BeerStore beerStore,
            @NonNull BeerListStore beerListStore) {
        return new BrewerBeersFetcher(networkApi,
                networkUtils,
                requestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named(Fetchers.REVIEW)
    static Fetcher<String> provideReviewFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull NetworkUtils networkUtils,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            ReviewStore reviewStore,
            ReviewListStore reviewListStore) {
        return new ReviewFetcher(networkApi,
                networkUtils,
                requestStatusStore::put,
                reviewStore,
                reviewListStore);
    }

    @Provides
    @Named(Fetchers.USER_REVIEWS)
    static Fetcher<String> provideReviewsFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull NetworkUtils networkUtils,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            @NonNull ReviewStore reviewStore,
            @NonNull ReviewListStore reviewListStore) {
        return new ReviewsFetcher(networkApi,
                networkUtils,
                requestStatusStore::put,
                reviewStore,
                reviewListStore);
    }

    @Provides
    @Named(Fetchers.TICKS)
    static Fetcher<String> provideTicksFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull NetworkUtils networkUtils,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            @NonNull BeerStore beerStore,
            @NonNull BeerListStore beerListStore,
            @NonNull UserStore userStore) {
        return new TicksFetcher(networkApi,
                networkUtils,
                requestStatusStore::put,
                beerStore,
                beerListStore,
                userStore);
    }

    @Provides
    @Named(Fetchers.TICK)
    static Fetcher<String> provideTickBeerFetcher(
            @NonNull NetworkApi networkApi,
            @NonNull NetworkUtils networkUtils,
            @NonNull NetworkRequestStatusStore requestStatusStore,
            @NonNull BeerStore beerStore,
            @NonNull BeerListStore beerListStore,
            @NonNull UserStore userStore) {
        return new TickBeerFetcher(networkApi,
                networkUtils,
                requestStatusStore::put,
                beerStore,
                beerListStore,
                userStore);
    }

    @Provides
    static FetcherManager provideFetcherManager(
            @Named(Fetchers.LOGIN) @NonNull Fetcher<String> loginFetcher,
            @Named(Fetchers.BEER) @NonNull Fetcher<String> beerFetcher,
            @Named(Fetchers.BEER_SEARCH) @NonNull Fetcher<String> beerSearchFetcher,
            @Named(Fetchers.BARCODE_SEARCH) @NonNull Fetcher<String> barcodeSearchFetcher,
            @Named(Fetchers.TOP_BEERS) @NonNull Fetcher<String> topBeersFetcher,
            @Named(Fetchers.BEERS_IN_COUNTRY) @NonNull Fetcher<String> beersInCountryFetcher,
            @Named(Fetchers.BEERS_IN_STYLE) @NonNull Fetcher<String> beersInStyleFetcher,
            @Named(Fetchers.BREWER) @NonNull Fetcher<String> brewerFetcher,
            @Named(Fetchers.BREWER_BEERS) @NonNull Fetcher<String> brewerBeersFetcher,
            @Named(Fetchers.REVIEW) @NonNull Fetcher<String> reviewFetcher,
            @Named(Fetchers.USER_REVIEWS) @NonNull Fetcher<String> userReviewsFetcher,
            @Named(Fetchers.TICKS) @NonNull Fetcher<String> ticksFetcher,
            @Named(Fetchers.TICK) @NonNull Fetcher<String> tickBeerFetcher) {
        List<Fetcher<String>> fetchers = Arrays.asList(
                loginFetcher,
                beerFetcher,
                beerSearchFetcher,
                barcodeSearchFetcher,
                topBeersFetcher,
                beersInCountryFetcher,
                beersInStyleFetcher,
                brewerFetcher,
                brewerBeersFetcher,
                reviewFetcher,
                userReviewsFetcher,
                ticksFetcher,
                tickBeerFetcher
        );

        return new FetcherManager(fetchers);
    }
}
