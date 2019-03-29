/**
 * This file is part of QuickBeer.
 * Copyright (C) 2019 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.injections

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.data.actions.BeerSearchActions
import quickbeer.android.viewmodels.SearchViewViewModel
import javax.inject.Named

@Module
class ActivityModule(private val activity: AppCompatActivity) {

    @Provides
    @ActivityScope
    internal fun provideActivity(): AppCompatActivity = activity

    @Provides
    @ActivityScope
    internal fun provideSearchViewViewModel(beerSearchActions: BeerSearchActions) =
        SearchViewViewModel(beerSearchActions)

    @Provides
    @ActivityScope
    internal fun provideAnalytics(activity: AppCompatActivity) = Analytics(activity)

    @Provides
    @IdRes
    @Named("fragmentContainer")
    internal fun provideNavigationContainerId(): Int = R.id.container
}
