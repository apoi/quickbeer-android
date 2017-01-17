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

import java.net.CookieManager;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reark.reark.network.fetchers.Fetcher;
import io.reark.reark.network.fetchers.UriFetcherManager;
import quickbeer.android.data.stores.BeerListStore;
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
    public Fetcher provideLoginFetcher(NetworkApi networkApi,
                                       CookieManager cookieManager,
                                       NetworkRequestStatusStore networkRequestStatusStore,
                                       UserSettingsStore userSettingsStore) {
        return new LoginFetcher(networkApi,
                cookieManager,
                networkRequestStatusStore::put,
                userSettingsStore);
    }

    @Provides
    @Named("beerFetcher")
    public Fetcher provideBeerFetcher(NetworkApi networkApi,
                                      NetworkUtils networkUtils,
                                      NetworkRequestStatusStore networkRequestStatusStore,
                                      BeerStore beerStore) {
        return new BeerFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore);
    }

    @Provides
    @Named("beerSearchFetcher")
    public Fetcher provideBeerSearchFetcher(NetworkApi networkApi,
                                            NetworkUtils networkUtils,
                                            NetworkRequestStatusStore networkRequestStatusStore,
                                            BeerStore beerStore,
                                            BeerListStore beerListStore) {
        return new BeerSearchFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named("topBeersFetcher")
    public Fetcher provideTopBeersFetcher(NetworkApi networkApi,
                                          NetworkUtils networkUtils,
                                          NetworkRequestStatusStore networkRequestStatusStore,
                                          BeerStore beerStore,
                                          BeerListStore beerListStore) {
        return new TopBeersFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named("beersInCountryFetcher")
    public Fetcher provideBeersInCountryFetcher(NetworkApi networkApi,
                                                NetworkUtils networkUtils,
                                                NetworkRequestStatusStore networkRequestStatusStore,
                                                BeerStore beerStore,
                                                BeerListStore beerListStore) {
        return new BeersInCountryFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named("beersInStyleFetcher")
    public Fetcher provideBeersInStyleFetcher(NetworkApi networkApi,
                                              NetworkUtils networkUtils,
                                              NetworkRequestStatusStore networkRequestStatusStore,
                                              BeerStore beerStore,
                                              BeerListStore beerListStore) {
        return new BeersInStyleFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    @Named("reviewFetcher")
    public Fetcher provideReviewFetcher(NetworkApi networkApi,
                                        NetworkUtils networkUtils,
                                        NetworkRequestStatusStore networkRequestStatusStore,
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
    public Fetcher provideTicksFetcher(NetworkApi networkApi,
                                       NetworkUtils networkUtils,
                                       NetworkRequestStatusStore networkRequestStatusStore,
                                       BeerStore beerStore,
                                       BeerListStore beerListStore) {
        return new TicksFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerListStore);
    }

    @Provides
    public UriFetcherManager provideUriFetcherManager(@Named("loginFetcher") Fetcher loginFetcher,
                                                      @Named("beerFetcher") Fetcher beerFetcher,
                                                      @Named("beerSearchFetcher") Fetcher beerSearchFetcher,
                                                      @Named("topBeersFetcher") Fetcher topBeersFetcher,
                                                      @Named("beersInCountryFetcher") Fetcher beersInCountryFetcher,
                                                      @Named("beersInStyleFetcher") Fetcher beersInStyleFetcher,
                                                      @Named("reviewFetcher") Fetcher reviewFetcher,
                                                      @Named("ticksFetcher") Fetcher ticksFetcher) {
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
