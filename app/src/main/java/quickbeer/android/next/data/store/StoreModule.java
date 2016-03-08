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
package quickbeer.android.next.data.store;

import android.content.ContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class StoreModule {
    @Provides
    @Singleton
    public UserSettingsStore provideUserSettingsStore(ContentResolver contentResolver) {
        return new UserSettingsStore(contentResolver);
    }

    @Provides
    @Singleton
    public NetworkRequestStatusStore provideNetworkRequestStatusStore(ContentResolver contentResolver) {
        return new NetworkRequestStatusStore(contentResolver);
    }

    @Provides
    @Singleton
    public BeerStore provideBeerStore(ContentResolver contentResolver) {
        return new BeerStore(contentResolver);
    }

    @Provides
    @Singleton
    public BeerSearchStore provideBeerSearchStore(ContentResolver contentResolver) {
        return new BeerSearchStore(contentResolver);
    }

    @Provides
    @Singleton
    public ReviewStore provideReviewStore(ContentResolver contentResolver) {
        return new ReviewStore(contentResolver);
    }

    @Provides
    @Singleton
    public ReviewListStore provideReviewListStore(ContentResolver contentResolver) {
        return new ReviewListStore(contentResolver);
    }
}
