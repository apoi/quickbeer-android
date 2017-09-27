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
import quickbeer.android.data.access.ServiceDataLayer;
import quickbeer.android.data.actions.BarcodeActions;
import quickbeer.android.data.actions.BeerActions;
import quickbeer.android.data.actions.BeerListActions;
import quickbeer.android.data.actions.BeerSearchActions;
import quickbeer.android.data.actions.BrewerActions;
import quickbeer.android.data.actions.BrewerListActions;
import quickbeer.android.data.actions.CountryActions;
import quickbeer.android.data.actions.ReviewActions;
import quickbeer.android.data.actions.StyleActions;
import quickbeer.android.data.actions.UserActions;
import quickbeer.android.data.actions.impl.BarcodeActionsImpl;
import quickbeer.android.data.actions.impl.BeerActionsImpl;
import quickbeer.android.data.actions.impl.BeerListActionsImpl;
import quickbeer.android.data.actions.impl.BeerSearchActionsImpl;
import quickbeer.android.data.actions.impl.BrewerActionsImpl;
import quickbeer.android.data.actions.impl.BrewerListActionsImpl;
import quickbeer.android.data.actions.impl.CountryActionsImpl;
import quickbeer.android.data.actions.impl.ReviewActionsImpl;
import quickbeer.android.data.actions.impl.StyleActionsImpl;
import quickbeer.android.data.actions.impl.UserActionsImpl;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.data.stores.BeerStyleStore;
import quickbeer.android.data.stores.BrewerMetadataStore;
import quickbeer.android.data.stores.BrewerStore;
import quickbeer.android.data.stores.CountryStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.data.stores.ReviewStore;
import quickbeer.android.data.stores.StoreModule;
import quickbeer.android.data.stores.UserStore;
import quickbeer.android.injections.ForApplication;
import quickbeer.android.network.fetchers.FetcherModule;

@Module(includes = { FetcherModule.class, StoreModule.class })
public final class DataModule {

    @Provides
    static BarcodeActions provideBarcodeActions(
            @ForApplication @NonNull Context context,
            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull BeerListStore beerListStore) {
        return new BarcodeActionsImpl(context, networkRequestStatusStore, beerListStore);
    }

    @Provides
    static BeerActions provideBeerActions(
            @ForApplication @NonNull Context context,
            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull BeerStore beerStore,
            @NonNull BeerMetadataStore beerMetadataStore,
            @NonNull ReviewStore reviewStore,
            @NonNull ReviewListStore reviewListStore) {
        return new BeerActionsImpl(context, networkRequestStatusStore, beerStore, beerMetadataStore, reviewStore, reviewListStore);
    }

    @Provides
    static BeerListActions provideBeerListActions(
            @ForApplication @NonNull Context context,
            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull BeerListStore beerListStore,
            @NonNull BeerMetadataStore beerMetadataStore) {
        return new BeerListActionsImpl(context, networkRequestStatusStore, beerListStore, beerMetadataStore);
    }

    @Provides
    static BeerSearchActions provideBeerSearchActions(
            @ForApplication @NonNull Context context,
            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull BeerListStore beerListStore) {
        return new BeerSearchActionsImpl(context, networkRequestStatusStore, beerListStore);
    }

    @Provides
    static BrewerActions providerBrewerActions(
            @ForApplication @NonNull Context context,
            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull BeerListStore beerListStore,
            @NonNull BeerMetadataStore beerMetadataStore,
            @NonNull BrewerStore brewerStore,
            @NonNull BrewerMetadataStore brewerMetadataStore) {
        return new BrewerActionsImpl(context, networkRequestStatusStore, beerListStore, beerMetadataStore, brewerStore, brewerMetadataStore);
    }

    @Provides
    static BrewerListActions providerBrewerListActions(
            @ForApplication @NonNull Context context,
            @NonNull BrewerMetadataStore brewerMetadataStore) {
        return new BrewerListActionsImpl(context, brewerMetadataStore);
    }

    @Provides
    static CountryActions provideCountryActions(
            @ForApplication @NonNull Context context,
            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull BeerListStore beerListStore,
            @NonNull CountryStore countryStore) {
        return new CountryActionsImpl(context, networkRequestStatusStore, beerListStore, countryStore);
    }

    @Provides
    static ReviewActions provideReviewActions(
            @ForApplication @NonNull Context context,
            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull BeerListStore beerListStore,
            @NonNull ReviewStore reviewStore) {
        return new ReviewActionsImpl(context, networkRequestStatusStore, beerListStore, reviewStore);
    }

    @Provides
    static StyleActions provideStyleActions(
            @ForApplication @NonNull Context context,
            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull BeerListStore beerListStore,
            @NonNull BeerStyleStore beerStyleStore) {
        return new StyleActionsImpl(context, networkRequestStatusStore, beerListStore, beerStyleStore);
    }

    @Provides
    static UserActions provideUserActions(
            @ForApplication @NonNull Context context,
            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
            @NonNull UserStore userStore) {
        return new UserActionsImpl(context, networkRequestStatusStore, userStore);
    }

    @Provides
    @Singleton
    static ServiceDataLayer provideServiceDataLayer(
            @NonNull UriFetcherManager fetcherManager) {
        return new ServiceDataLayer(fetcherManager);
    }
}
