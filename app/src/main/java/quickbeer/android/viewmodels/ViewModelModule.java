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
package quickbeer.android.viewmodels;

import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import quickbeer.android.data.DataLayer;
import quickbeer.android.features.beerdetails.BeerDetailsViewModel;
import quickbeer.android.providers.GlobalNotificationProvider;
import quickbeer.android.providers.ProgressStatusProvider;
import quickbeer.android.providers.ResourceProvider;

@Module
public final class ViewModelModule {

    @Provides
    static RecentBeersViewModel provideRecentBeersViewModel(
            @NonNull DataLayer.GetBeer getBeer,
            @NonNull DataLayer.GetAccessedBeers getAccessedBeers,
            @NonNull ProgressStatusProvider progressStatusProvider) {
        return new RecentBeersViewModel(getBeer, getAccessedBeers, progressStatusProvider);
    }

    @Provides
    static RecentBrewersViewModel provideRecentBrewersViewModel(
            @NonNull DataLayer.GetBeer getBeer,
            @NonNull DataLayer.GetBrewer getBrewer,
            @NonNull DataLayer.GetAccessedBrewers getAccessedBrewers,
            @NonNull ProgressStatusProvider progressStatusProvider) {
        return new RecentBrewersViewModel(getBeer, getBrewer, getAccessedBrewers, progressStatusProvider);
    }

    @Provides
    static BeerSearchViewModel provideBeerSearchViewModel(
            @NonNull DataLayer.GetBeer getBeer,
            @NonNull DataLayer.GetBeerSearch getBeerSearch,
            @NonNull SearchViewViewModel searchViewViewModel,
            @NonNull ProgressStatusProvider progressStatusProvider) {
        return new BeerSearchViewModel(getBeer, getBeerSearch, searchViewViewModel, progressStatusProvider);
    }

    @Provides
    static BarcodeSearchViewModel provideBarcodeSearchViewModel(
            @NonNull DataLayer.GetBeer getBeer,
            @NonNull DataLayer.GetBeerSearch getBeerSearch,
            @NonNull DataLayer.GetBarcodeSearch getBarcodeSearch,
            @NonNull SearchViewViewModel searchViewViewModel,
            @NonNull ProgressStatusProvider progressStatusProvider) {
        return new BarcodeSearchViewModel(getBeer, getBeerSearch, getBarcodeSearch, searchViewViewModel, progressStatusProvider);
    }

    @Provides
    static TopBeersViewModel provideTopBeersViewModel(
            @NonNull DataLayer.GetBeer getBeer,
            @NonNull DataLayer.GetBeerSearch getBeerSearch,
            @NonNull DataLayer.GetTopBeers getTopBeers,
            @NonNull SearchViewViewModel searchViewViewModel,
            @NonNull ProgressStatusProvider progressStatusProvider) {
        return new TopBeersViewModel(getBeer, getBeerSearch, getTopBeers, searchViewViewModel, progressStatusProvider);
    }

    @Provides
    static BeerDetailsViewModel provideBeerDetailsViewModel(
            @NonNull DataLayer.GetBeer getBeer,
            @NonNull DataLayer.TickBeer tickBeer,
            @NonNull DataLayer.GetBrewer getBrewer,
            @NonNull DataLayer.GetUser getUser,
            @NonNull DataLayer.GetReviews getReviews,
            @NonNull DataLayer.GetReview getReview,
            @NonNull ResourceProvider resourceProvider,
            @NonNull GlobalNotificationProvider notificationProvider) {
        return new BeerDetailsViewModel(getBeer, tickBeer, getBrewer, getUser, getReviews, getReview, resourceProvider, notificationProvider);
    }

    @Provides
    static BrewerViewModel providerBrewerViewModel(
            @NonNull DataLayer.GetBeer getBeer,
            @NonNull DataLayer.GetBrewer getBrewer) {
        return new BrewerViewModel(getBeer, getBrewer);
    }

}
