/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela></antti.poikela>@iki.fi>
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
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.network.fetchers

import com.franmontiel.persistentcookiejar.ClearableCookieJar
import dagger.Module
import dagger.Provides
import io.reactivex.functions.Consumer
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.BeerMetadataStore
import quickbeer.android.data.stores.BeerStore
import quickbeer.android.data.stores.BrewerMetadataStore
import quickbeer.android.data.stores.BrewerStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.data.stores.ReviewListStore
import quickbeer.android.data.stores.ReviewStore
import quickbeer.android.data.stores.UserStore
import quickbeer.android.network.NetworkApi
import quickbeer.android.network.NetworkModule
import quickbeer.android.network.fetchers.impl.BarcodeSearchFetcher
import quickbeer.android.network.fetchers.impl.BeerFetcher
import quickbeer.android.network.fetchers.impl.BeerSearchFetcher
import quickbeer.android.network.fetchers.impl.BeersInCountryFetcher
import quickbeer.android.network.fetchers.impl.BeersInStyleFetcher
import quickbeer.android.network.fetchers.impl.BrewerBeersFetcher
import quickbeer.android.network.fetchers.impl.BrewerFetcher
import quickbeer.android.network.fetchers.impl.LoginFetcher
import quickbeer.android.network.fetchers.impl.ReviewFetcher
import quickbeer.android.network.fetchers.impl.ReviewsFetcher
import quickbeer.android.network.fetchers.impl.TickBeerFetcher
import quickbeer.android.network.fetchers.impl.TicksFetcher
import quickbeer.android.network.fetchers.impl.TopBeersFetcher
import quickbeer.android.network.utils.NetworkUtils

@Module(includes = [NetworkModule::class])
class FetcherModule {

    @Provides
    fun provideLoginFetcher(
        networkApi: NetworkApi,
        cookieJar: ClearableCookieJar,
        requestStatusStore: NetworkRequestStatusStore,
        userStore: UserStore
    ): LoginFetcher = LoginFetcher(
        networkApi,
        cookieJar,
        Consumer { requestStatusStore.put(it) },
        userStore)

    @Provides
    fun provideBeerFetcher(
        networkApi: NetworkApi,
        networkUtils: NetworkUtils,
        requestStatusStore: NetworkRequestStatusStore,
        beerStore: BeerStore,
        metadataStore: BeerMetadataStore
    ): BeerFetcher = BeerFetcher(
        networkApi,
        networkUtils,
        Consumer { requestStatusStore.put(it) },
        beerStore,
        metadataStore)

    @Provides
    fun provideBeerSearchFetcher(
        networkApi: NetworkApi,
        networkUtils: NetworkUtils,
        requestStatusStore: NetworkRequestStatusStore,
        beerStore: BeerStore,
        beerListStore: BeerListStore
    ): BeerSearchFetcher = BeerSearchFetcher(
        networkApi,
        networkUtils,
        Consumer { requestStatusStore.put(it) },
        beerStore,
        beerListStore)

    @Provides
    fun provideBarcodeSearchFetcher(
        networkApi: NetworkApi,
        networkUtils: NetworkUtils,
        requestStatusStore: NetworkRequestStatusStore,
        beerStore: BeerStore,
        beerListStore: BeerListStore
    ): BarcodeSearchFetcher = BarcodeSearchFetcher(
        networkApi,
        networkUtils,
        Consumer { requestStatusStore.put(it) },
        beerStore,
        beerListStore)

    @Provides
    fun provideTopBeersFetcher(
        networkApi: NetworkApi,
        networkUtils: NetworkUtils,
        requestStatusStore: NetworkRequestStatusStore,
        beerStore: BeerStore,
        beerListStore: BeerListStore
    ): TopBeersFetcher = TopBeersFetcher(
        networkApi,
        networkUtils,
        Consumer { requestStatusStore.put(it) },
        beerStore,
        beerListStore)

    @Provides
    fun provideBeersInCountryFetcher(
        networkApi: NetworkApi,
        networkUtils: NetworkUtils,
        requestStatusStore: NetworkRequestStatusStore,
        beerStore: BeerStore,
        beerListStore: BeerListStore
    ): BeersInCountryFetcher = BeersInCountryFetcher(
        networkApi,
        networkUtils,
        Consumer { requestStatusStore.put(it) },
        beerStore,
        beerListStore)

    @Provides
    fun provideBeersInStyleFetcher(
        networkApi: NetworkApi,
        networkUtils: NetworkUtils,
        requestStatusStore: NetworkRequestStatusStore,
        beerStore: BeerStore,
        beerListStore: BeerListStore
    ): BeersInStyleFetcher = BeersInStyleFetcher(
        networkApi,
        networkUtils,
        Consumer { requestStatusStore.put(it) },
        beerStore,
        beerListStore)

    @Provides
    fun provideBrewerFetcher(
        networkApi: NetworkApi,
        networkUtils: NetworkUtils,
        requestStatusStore: NetworkRequestStatusStore,
        brewerStore: BrewerStore,
        metadataStore: BrewerMetadataStore
    ): BrewerFetcher = BrewerFetcher(
        networkApi,
        networkUtils,
        Consumer { requestStatusStore.put(it) },
        brewerStore,
        metadataStore)

    @Provides
    fun provideBrewerBeersFetcher(
        networkApi: NetworkApi,
        networkUtils: NetworkUtils,
        requestStatusStore: NetworkRequestStatusStore,
        beerStore: BeerStore,
        beerListStore: BeerListStore
    ): BrewerBeersFetcher = BrewerBeersFetcher(
        networkApi,
        networkUtils,
        Consumer { requestStatusStore.put(it) },
        beerStore,
        beerListStore)

    @Provides
    fun provideReviewFetcher(
        networkApi: NetworkApi,
        networkUtils: NetworkUtils,
        requestStatusStore: NetworkRequestStatusStore,
        reviewStore: ReviewStore,
        reviewListStore: ReviewListStore
    ): ReviewFetcher = ReviewFetcher(
        networkApi,
        networkUtils,
        Consumer { requestStatusStore.put(it) },
        reviewStore,
        reviewListStore)

    @Provides
    fun provideReviewsFetcher(
        networkApi: NetworkApi,
        networkUtils: NetworkUtils,
        requestStatusStore: NetworkRequestStatusStore,
        reviewStore: ReviewStore,
        reviewListStore: ReviewListStore
    ): ReviewsFetcher = ReviewsFetcher(
        networkApi,
        networkUtils,
        Consumer { requestStatusStore.put(it) },
        reviewStore,
        reviewListStore)

    @Provides
    fun provideTicksFetcher(
        networkApi: NetworkApi,
        networkUtils: NetworkUtils,
        requestStatusStore: NetworkRequestStatusStore,
        beerStore: BeerStore,
        beerListStore: BeerListStore,
        userStore: UserStore
    ): TicksFetcher = TicksFetcher(
        networkApi,
        networkUtils,
        Consumer { requestStatusStore.put(it) },
        beerStore,
        beerListStore,
        userStore)

    @Provides
    fun provideTickBeerFetcher(
        networkApi: NetworkApi,
        networkUtils: NetworkUtils,
        requestStatusStore: NetworkRequestStatusStore,
        beerStore: BeerStore,
        beerListStore: BeerListStore,
        userStore: UserStore
    ): TickBeerFetcher = TickBeerFetcher(
        networkApi,
        networkUtils,
        Consumer { requestStatusStore.put(it) },
        beerStore,
        beerListStore,
        userStore)

    @Provides
    fun provideFetcherManager(
        loginFetcher: LoginFetcher,
        beerFetcher: BeerFetcher,
        beerSearchFetcher: BeerSearchFetcher,
        barcodeSearchFetcher: BarcodeSearchFetcher,
        topBeersFetcher: TopBeersFetcher,
        beersInCountryFetcher: BeersInCountryFetcher,
        beersInStyleFetcher: BeersInStyleFetcher,
        brewerFetcher: BrewerFetcher,
        brewerBeersFetcher: BrewerBeersFetcher,
        reviewFetcher: ReviewFetcher,
        userReviewsFetcher: ReviewsFetcher,
        ticksFetcher: TicksFetcher,
        tickBeerFetcher: TickBeerFetcher
    ): FetcherManager {
        val fetchers = listOf(
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
        )

        return FetcherManager(fetchers)
    }
}
