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
package quickbeer.android.next.injections;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.activities.BeerDetailsActivity;
import quickbeer.android.next.activities.base.SearchActivity;
import quickbeer.android.next.data.DataStoreModule;
import quickbeer.android.next.fragments.BeerDetailsFragment;
import quickbeer.android.next.fragments.BeerListFragment;
import quickbeer.android.next.fragments.BeerSearchFragment;
import quickbeer.android.next.fragments.MainFragment;
import quickbeer.android.next.fragments.TopBeersFragment;
import quickbeer.android.next.fragments.TopInCountryFragment;
import quickbeer.android.next.fragments.TopListFragment;
import quickbeer.android.next.network.NetworkService;
import quickbeer.android.next.utils.UtilsModule;
import quickbeer.android.next.viewmodels.ViewModelModule;
import quickbeer.android.next.views.CountryListView;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        UtilsModule.class,
        DataStoreModule.class,
        ViewModelModule.class,
        InstrumentationModule.class
})
public interface Graph {

    void inject(QuickBeer quickBeer);
    void inject(NetworkService networkService);

    void inject(SearchActivity searchActivityBase);
    void inject(BeerDetailsActivity beerDetailsActivity);

    void inject(MainFragment mainFragment);
    void inject(BeerListFragment beerListFragment);
    void inject(BeerSearchFragment beerSearchFragment);
    void inject(BeerDetailsFragment beerDetailsFragment);
    void inject(TopListFragment topListFragment);
    void inject(TopBeersFragment topBeersFragment);
    void inject(TopInCountryFragment topInCountryFragment);

    void inject(CountryListView countryListView);

    final class Initializer {
        public static Graph init(Application application) {
            return DaggerGraph.builder()
                    .applicationModule(new ApplicationModule(application))
                    .build();
        }
    }
}
