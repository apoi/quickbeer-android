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
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.data.DataStoreModule;
import quickbeer.android.next.network.NetworkModule;
import quickbeer.android.next.network.NetworkService;
import quickbeer.android.next.network.ServiceDataLayer;
import quickbeer.android.next.utils.UtilsModule;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        NetworkModule.class,
        InstrumentationModule.class,
        UtilsModule.class,
        DataStoreModule.class,
})
public interface ApplicationGraph {
    DataLayer provideApplicationDataLayer();
    DataLayer.GetBeer provideGetBeer();
    DataLayer.GetBeerSearch provideGetBeerSearch();
    DataLayer.GetBeerSearchQueries provideGetBeerSearchQueries();
    DataLayer.GetBeersInCountry provideGetBeersInCountry();
    DataLayer.GetBeersInStyle provideGetBeersInStyle();
    DataLayer.GetReview provideGetReview();
    DataLayer.GetReviews provideGetReviews();
    DataLayer.GetTopBeers provideGetTopBeers();
    DataLayer.GetUserSettings provideGetUserSettings();
    DataLayer.SetUserSettings provideSetUserSettings();
    ServiceDataLayer provideServiceDataLayer();

    void inject(QuickBeer quickBeer);
    void inject(NetworkService networkService);

    final class Initializer {
        public static ApplicationGraph init(Application application) {
            return DaggerApplicationGraph.builder()
                    .applicationModule(new ApplicationModule(application))
                    .build();
        }
    }
}
