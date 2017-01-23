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
package quickbeer.android.injections;

import android.app.Activity;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import quickbeer.android.data.DataLayer;
import quickbeer.android.features.home.SearchViewModel;

@Module
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(@NonNull final Activity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    Activity providesActivity() {
        return activity;
    }

    @Provides
    @ActivityScope
    static SearchViewModel providesSearchViewModel(
            @NonNull final DataLayer.GetBeerSearchQueries getBeerSearchQueries) {
        return new SearchViewModel(getBeerSearchQueries);
    }
}
