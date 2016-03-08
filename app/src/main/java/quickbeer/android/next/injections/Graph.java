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
import quickbeer.android.next.activities.ActivityBase;
import quickbeer.android.next.activities.BeerDetailsActivity;
import quickbeer.android.next.data.DataStoreModule;
import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;
import quickbeer.android.next.fragments.BeerDetailsFragment;
import quickbeer.android.next.fragments.BeerListFragment;
import quickbeer.android.next.fragments.BeerSearchFragment;
import quickbeer.android.next.fragments.MainFragment;
import quickbeer.android.next.fragments.TopBeersFragment;
import quickbeer.android.next.network.NetworkService;
import quickbeer.android.next.viewmodels.ViewModelModule;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        DataStoreModule.class,
        ViewModelModule.class,
        InstrumentationModule.class
})
public interface Graph {

    void inject(QuickBeer quickBeer);
    void inject(NetworkService networkService);

    void inject(ActivityBase activityBase);
    void inject(BeerDetailsActivity beerDetailsActivity);

    void inject(MainFragment mainFragment);
    void inject(BeerListFragment beerListFragment);
    void inject(BeerSearchFragment beerSearchFragment);
    void inject(BeerDetailsFragment beerDetailsFragment);
    void inject(TopBeersFragment topBeersFragment);

    void inject(BeerStore store);
    void inject(BeerSearchStore store);
    void inject(ReviewStore store);
    void inject(ReviewListStore store);
    void inject(NetworkRequestStatusStore store);

    final class Initializer {
        public static Graph init(Application application) {
            return DaggerGraph.builder()
                    .applicationModule(new ApplicationModule(application))
                    .build();
        }
    }
}
