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
package quickbeer.android.data

import android.content.Context
import dagger.Module
import dagger.Provides
import quickbeer.android.data.access.ServiceDataLayer
import quickbeer.android.data.actions.BarcodeActions
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.BeerListActions
import quickbeer.android.data.actions.BeerSearchActions
import quickbeer.android.data.actions.BrewerActions
import quickbeer.android.data.actions.BrewerListActions
import quickbeer.android.data.actions.CountryActions
import quickbeer.android.data.actions.ReviewActions
import quickbeer.android.data.actions.StyleActions
import quickbeer.android.data.actions.UserActions
import quickbeer.android.data.actions.impl.BarcodeActionsImpl
import quickbeer.android.data.actions.impl.BeerActionsImpl
import quickbeer.android.data.actions.impl.BeerListActionsImpl
import quickbeer.android.data.actions.impl.BeerSearchActionsImpl
import quickbeer.android.data.actions.impl.BrewerActionsImpl
import quickbeer.android.data.actions.impl.BrewerListActionsImpl
import quickbeer.android.data.actions.impl.CountryActionsImpl
import quickbeer.android.data.actions.impl.ReviewActionsImpl
import quickbeer.android.data.actions.impl.StyleActionsImpl
import quickbeer.android.data.actions.impl.UserActionsImpl
import quickbeer.android.data.stores.BeerListStore
import quickbeer.android.data.stores.BeerMetadataStore
import quickbeer.android.data.stores.BeerStore
import quickbeer.android.data.stores.BeerStyleStore
import quickbeer.android.data.stores.BrewerMetadataStore
import quickbeer.android.data.stores.BrewerStore
import quickbeer.android.data.stores.CountryStore
import quickbeer.android.data.stores.NetworkRequestStatusStore
import quickbeer.android.data.stores.ReviewListStore
import quickbeer.android.data.stores.ReviewStore
import quickbeer.android.data.stores.StoreModule
import quickbeer.android.data.stores.UserStore
import quickbeer.android.injections.ForApplication
import quickbeer.android.network.fetchers.FetcherManager
import quickbeer.android.network.fetchers.FetcherModule

import javax.inject.Singleton

@Module(includes = [FetcherModule::class, StoreModule::class])
class DataModule {

    @Provides
    fun provideBarcodeActions(
        @ForApplication context: Context,
        networkRequestStatusStore: NetworkRequestStatusStore,
        beerListStore: BeerListStore
    ): BarcodeActions = BarcodeActionsImpl(context, networkRequestStatusStore, beerListStore)

    @Provides
    fun provideBeerActions(
        @ForApplication context: Context,
        networkRequestStatusStore: NetworkRequestStatusStore,
        beerStore: BeerStore,
        beerMetadataStore: BeerMetadataStore,
        reviewStore: ReviewStore,
        reviewListStore: ReviewListStore
    ): BeerActions = BeerActionsImpl(context, networkRequestStatusStore, beerStore, beerMetadataStore, reviewListStore)

    @Provides
    fun provideBeerListActions(
        @ForApplication context: Context,
        networkRequestStatusStore: NetworkRequestStatusStore,
        beerListStore: BeerListStore,
        beerMetadataStore: BeerMetadataStore
    ): BeerListActions = BeerListActionsImpl(context, networkRequestStatusStore, beerListStore, beerMetadataStore)

    @Provides
    fun provideBeerSearchActions(
        @ForApplication context: Context,
        networkRequestStatusStore: NetworkRequestStatusStore,
        beerListStore: BeerListStore
    ): BeerSearchActions = BeerSearchActionsImpl(context, networkRequestStatusStore, beerListStore)

    @Provides
    fun providerBrewerActions(
        @ForApplication context: Context,
        networkRequestStatusStore: NetworkRequestStatusStore,
        beerListStore: BeerListStore,
        brewerStore: BrewerStore,
        brewerMetadataStore: BrewerMetadataStore
    ): BrewerActions =
        BrewerActionsImpl(context, networkRequestStatusStore, beerListStore, brewerStore, brewerMetadataStore)

    @Provides
    fun providerBrewerListActions(
        @ForApplication context: Context,
        brewerMetadataStore: BrewerMetadataStore
    ): BrewerListActions = BrewerListActionsImpl(context, brewerMetadataStore)

    @Provides
    fun provideCountryActions(
        @ForApplication context: Context,
        networkRequestStatusStore: NetworkRequestStatusStore,
        beerListStore: BeerListStore,
        countryStore: CountryStore
    ): CountryActions = CountryActionsImpl(context, networkRequestStatusStore, beerListStore, countryStore)

    @Provides
    fun provideReviewActions(
        @ForApplication context: Context,
        networkRequestStatusStore: NetworkRequestStatusStore,
        beerListStore: BeerListStore,
        reviewStore: ReviewStore
    ): ReviewActions = ReviewActionsImpl(context, networkRequestStatusStore, beerListStore, reviewStore)

    @Provides
    fun provideStyleActions(
        @ForApplication context: Context,
        networkRequestStatusStore: NetworkRequestStatusStore,
        beerListStore: BeerListStore,
        beerStyleStore: BeerStyleStore
    ): StyleActions = StyleActionsImpl(context, networkRequestStatusStore, beerListStore, beerStyleStore)

    @Provides
    fun provideUserActions(
        @ForApplication context: Context,
        networkRequestStatusStore: NetworkRequestStatusStore,
        userStore: UserStore
    ): UserActions = UserActionsImpl(context, networkRequestStatusStore, userStore)

    @Provides
    @Singleton
    fun provideServiceDataLayer(
        fetcherManager: FetcherManager
    ): ServiceDataLayer = ServiceDataLayer(fetcherManager)
}
