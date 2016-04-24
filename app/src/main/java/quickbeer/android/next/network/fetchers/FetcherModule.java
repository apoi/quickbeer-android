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

import android.net.Uri;

import java.net.CookieManager;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reark.reark.network.fetchers.Fetcher;
import io.reark.reark.network.fetchers.UriFetcherManager;
import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;
import quickbeer.android.next.data.store.UserSettingsStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.NetworkModule;
import quickbeer.android.next.network.utils.NetworkUtils;

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
                                            BeerSearchStore beerSearchStore) {
        return new BeerSearchFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerSearchStore);
    }

    @Provides
    @Named("topBeersFetcher")
    public Fetcher provideTopBeersFetcher(NetworkApi networkApi,
                                          NetworkUtils networkUtils,
                                          NetworkRequestStatusStore networkRequestStatusStore,
                                          BeerStore beerStore,
                                          BeerSearchStore beerSearchStore) {
        return new TopBeersFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerSearchStore);
    }

    @Provides
    @Named("beersInCountryFetcher")
    public Fetcher provideBeersInCountryFetcher(NetworkApi networkApi,
                                                NetworkUtils networkUtils,
                                                NetworkRequestStatusStore networkRequestStatusStore,
                                                BeerStore beerStore,
                                                BeerSearchStore beerSearchStore) {
        return new BeersInCountryFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerSearchStore);
    }

    @Provides
    @Named("beersInStyleFetcher")
    public Fetcher provideBeersInStyleFetcher(NetworkApi networkApi,
                                              NetworkUtils networkUtils,
                                              NetworkRequestStatusStore networkRequestStatusStore,
                                              BeerStore beerStore,
                                              BeerSearchStore beerSearchStore) {
        return new BeersInStyleFetcher(networkApi,
                networkUtils,
                networkRequestStatusStore::put,
                beerStore,
                beerSearchStore);
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
    public UriFetcherManager provideUriFetcherManager(@Named("loginFetcher") Fetcher loginFetcher,
                                                      @Named("beerFetcher") Fetcher beerFetcher,
                                                      @Named("beerSearchFetcher") Fetcher beerSearchFetcher,
                                                      @Named("topBeersFetcher") Fetcher topBeersFetcher,
                                                      @Named("beersInCountryFetcher") Fetcher beersInCountryFetcher,
                                                      @Named("beersInStyleFetcher") Fetcher beersInStyleFetcher,
                                                      @Named("reviewFetcher") Fetcher reviewFetcher) {
        final List<Fetcher<Uri>> fetchers = Arrays.asList(
                loginFetcher,
                beerFetcher,
                beerSearchFetcher,
                topBeersFetcher,
                beersInCountryFetcher,
                beersInStyleFetcher,
                reviewFetcher
        );

        return new UriFetcherManager.Builder()
                .fetchers(fetchers)
                .build();
    }
}
