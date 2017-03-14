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

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import quickbeer.android.R;
import quickbeer.android.data.DataLayer;
import quickbeer.android.viewmodels.SearchViewViewModel;

@Module
public class ActivityModule {

    private final AppCompatActivity activity;

    public ActivityModule(@NonNull AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    AppCompatActivity providesActivity() {
        return activity;
    }

    @Provides
    @ActivityScope
    static SearchViewViewModel providesSearchViewViewModel(
            @NonNull DataLayer.GetBeerSearchQueries getBeerSearchQueries) {
        return new SearchViewViewModel(getBeerSearchQueries);
    }

    @Provides
    @IdRes
    @Named("fragmentContainer")
    static Integer providesNavigationContainerId() {
        return R.id.container;
    }

}
