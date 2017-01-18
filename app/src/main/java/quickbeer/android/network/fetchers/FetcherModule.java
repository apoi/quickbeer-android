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
import android.support.annotation.NonNull;

import java.net.CookieManager;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reark.reark.network.fetchers.Fetcher;
import io.reark.reark.network.fetchers.UriFetcherManager;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.data.stores.ReviewStore;
import quickbeer.android.data.stores.UserSettingsStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.NetworkModule;
import quickbeer.android.network.utils.NetworkUtils;

@Module(includes = NetworkModule.class)
public final class FetcherModule {

    @Provides
    @Named("loginFetcher")
    static Fetcher<Uri> provideLoginFetcher(
            @NonNull final NetworkApi networkApi,
            @NonNull final CookieManager cookieManager,
            @NonNull final NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull final UserSettingsStore userSettingsStore) {
        return new LoginFetcher(networkApi,
                cookieManager,
                networkRequestStatusStore::put,
                userSettingsStore);
    }

    @Provides
    @Named("beerFetcher")
    static Fetcher<Uri> provideBeerFetcher(
            @NonNull final NetworkApi networkApi,
            @NonNull final NetworkUtils networkUtils,
            @NonNull final NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull final BeerStore beerStore,
            @NonNull final BeerMetadataStore metadataStore) {
        return new BeerFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                metadataStore);
    }

    @Provides
    @Named("beerSearchFetcher")
    static Fetcher<Uri> provideBeerSearchFetcher(
            @NonNull final NetworkApi networkApi,
            @NonNull final NetworkUtils networkUtils,
            @NonNull final NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull final BeerStore beerStore,
            @NonNull final BeerListStore beerListStore) {
        return new BeerSearchFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named("topBeersFetcher")
    static Fetcher<Uri> provideTopBeersFetcher(
            @NonNull final NetworkApi networkApi,
            @NonNull final NetworkUtils networkUtils,
            @NonNull final NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull final BeerStore beerStore,
            @NonNull final BeerListStore beerListStore) {
        return new TopBeersFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named("beersInCountryFetcher")
    static Fetcher<Uri> provideBeersInCountryFetcher(
            @NonNull final NetworkApi networkApi,
            @NonNull final NetworkUtils networkUtils,
            @NonNull final NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull final BeerStore beerStore,
            @NonNull final BeerListStore beerListStore) {
        return new BeersInCountryFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named("beersInStyleFetcher")
    static Fetcher<Uri> provideBeersInStyleFetcher(
            @NonNull final NetworkApi networkApi,
            @NonNull final NetworkUtils networkUtils,
            @NonNull final NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull final BeerStore beerStore,
            @NonNull final BeerListStore beerListStore) {
        return new BeersInStyleFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named("reviewFetcher")
    static Fetcher<Uri> provideReviewFetcher(
            @NonNull final NetworkApi networkApi,
            @NonNull final NetworkUtils networkUtils,
            @NonNull final NetworkRequestStatusStore networkRequestStatusStore,
            ReviewStore reviewStore,
            ReviewListStore reviewListStore) {
        return new ReviewFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                reviewStore,
                reviewListStore);
    }

    @Provides
    @Named("ticksFetcher")
    static Fetcher<Uri> provideTicksFetcher(
            @NonNull final NetworkApi networkApi,
            @NonNull final NetworkUtils networkUtils,
            @NonNull final NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull final BeerStore beerStore,
            @NonNull final BeerListStore beerListStore) {
        return new TicksFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    static UriFetcherManager provideUriFetcherManager(
            @Named("loginFetcher") @NonNull final Fetcher<Uri> loginFetcher,
            @Named("beerFetcher") @NonNull final Fetcher<Uri> beerFetcher,
            @Named("beerSearchFetcher") @NonNull final Fetcher<Uri> beerSearchFetcher,
            @Named("topBeersFetcher") @NonNull final Fetcher<Uri> topBeersFetcher,
            @Named("beersInCountryFetcher") @NonNull final Fetcher<Uri> beersInCountryFetcher,
            @Named("beersInStyleFetcher") @NonNull final Fetcher<Uri> beersInStyleFetcher,
            @Named("reviewFetcher") @NonNull final Fetcher<Uri> reviewFetcher,
            @Named("ticksFetcher") @NonNull final Fetcher<Uri> ticksFetcher) {
        final List<Fetcher<Uri>> fetchers = Arrays.asList(
                loginFetcher,
                beerFetcher,
                beerSearchFetcher,
                topBeersFetcher,
                beersInCountryFetcher,
                beersInStyleFetcher,
                reviewFetcher,
                ticksFetcher
        );

        return new UriFetcherManager.Builder()
                .fetchers(fetchers)
                .build();
    }
}
