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
package quickbeer.android.data.stores;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.net.CookieManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class StoreModule {
    @Provides
    @Singleton
    static UserSettingsStore provideUserSettingsStore(
            @NonNull final ContentResolver contentResolver,
            @NonNull final Gson gson) {
        return new UserSettingsStore(contentResolver, gson);
    }

    @Provides
    @Singleton
    static NetworkRequestStatusStore provideNetworkRequestStatusStore(
            @NonNull final ContentResolver contentResolver,
            @NonNull final Gson gson) {
        return new NetworkRequestStatusStore(contentResolver, gson);
    }

    @Provides
    @Singleton
    static BeerStore provideBeerStore(
            @NonNull final ContentResolver contentResolver,
            @NonNull final Gson gson) {
        return new BeerStore(contentResolver, gson);
    }

    @Provides
    @Singleton
    static BeerListStore provideBeerListStore(
            @NonNull final ContentResolver contentResolver,
            @NonNull final Gson gson) {
        return new BeerListStore(contentResolver, gson);
    }

    @Provides
    @Singleton
    static BeerMetadataStore provideBeerMetadataStore(
            @NonNull final ContentResolver contentResolver,
            @NonNull final Gson gson) {
        return new BeerMetadataStore(contentResolver, gson);
    }

    @Provides
    @Singleton
    static ReviewStore provideReviewStore(
            @NonNull final ContentResolver contentResolver,
            @NonNull final Gson gson) {
        return new ReviewStore(contentResolver, gson);
    }

    @Provides
    @Singleton
    static ReviewListStore provideReviewListStore(
            @NonNull final ContentResolver contentResolver,
            @NonNull final Gson gson) {
        return new ReviewListStore(contentResolver, gson);
    }

    @Provides
    @Singleton
    static BrewerStore provideBrewerStore(
            @NonNull final ContentResolver contentResolver,
            @NonNull final Gson gson) {
        return new BrewerStore(contentResolver, gson);
    }

    @Provides
    @Singleton
    static BrewerListStore provideBrewerListStore(
            @NonNull final ContentResolver contentResolver,
            @NonNull final Gson gson) {
        return new BrewerListStore(contentResolver, gson);
    }

    @Provides
    @Singleton
    static BrewerMetadataStore provideBrewerMetadataStore(
            @NonNull final ContentResolver contentResolver,
            @NonNull final Gson gson) {
        return new BrewerMetadataStore(contentResolver, gson);
    }
}
