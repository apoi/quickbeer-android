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
package quickbeer.android.data.stores

import android.content.ContentResolver
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import quickbeer.android.providers.ResourceProvider
import javax.inject.Singleton

@Module
class StoreModule {

    @Provides
    @Singleton
    fun provideUserStore(
        contentResolver: ContentResolver,
        gson: Gson
    ): UserStore = UserStore(contentResolver, gson)

    @Provides
    @Singleton
    fun provideRequestStatusStore(
        contentResolver: ContentResolver,
        gson: Gson
    ): NetworkRequestStatusStore = NetworkRequestStatusStore(contentResolver, gson)

    @Provides
    @Singleton
    fun provideBeerStore(
        contentResolver: ContentResolver,
        gson: Gson
    ): BeerStore = BeerStore(contentResolver, gson)

    @Provides
    @Singleton
    fun provideBeerStyleStore(
        resourceProvider: ResourceProvider,
        gson: Gson
    ): BeerStyleStore = BeerStyleStore(resourceProvider, gson)

    @Provides
    @Singleton
    fun provideBeerListStore(
        contentResolver: ContentResolver,
        gson: Gson
    ): BeerListStore = BeerListStore(contentResolver, gson)

    @Provides
    @Singleton
    fun provideBeerMetadataStore(
        contentResolver: ContentResolver,
        gson: Gson
    ): BeerMetadataStore = BeerMetadataStore(contentResolver, gson)

    @Provides
    @Singleton
    fun provideCountryStore(
        resourceProvider: ResourceProvider,
        gson: Gson
    ): CountryStore = CountryStore(resourceProvider, gson)

    @Provides
    @Singleton
    fun provideReviewStore(
        contentResolver: ContentResolver,
        gson: Gson
    ): ReviewStore = ReviewStore(contentResolver, gson)

    @Provides
    @Singleton
    fun provideReviewListStore(
        contentResolver: ContentResolver,
        gson: Gson
    ): ReviewListStore = ReviewListStore(contentResolver, gson)

    @Provides
    @Singleton
    fun provideBrewerStore(
        contentResolver: ContentResolver,
        gson: Gson
    ): BrewerStore = BrewerStore(contentResolver, gson)

    @Provides
    @Singleton
    fun provideBrewerListStore(
        contentResolver: ContentResolver,
        gson: Gson
    ): BrewerListStore = BrewerListStore(contentResolver, gson)

    @Provides
    @Singleton
    fun provideBrewerMetadataStore(
        contentResolver: ContentResolver,
        gson: Gson
    ): BrewerMetadataStore = BrewerMetadataStore(contentResolver, gson)
}
