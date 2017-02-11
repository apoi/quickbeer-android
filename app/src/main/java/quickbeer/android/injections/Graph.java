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

import javax.inject.Singleton;

import dagger.Component;
import quickbeer.android.QuickBeer;
import quickbeer.android.data.DataStoreModule;
import quickbeer.android.network.NetworkModule;
import quickbeer.android.network.NetworkService;
import quickbeer.android.utils.UtilsModule;
import quickbeer.android.views.viewholders.BeerDetailsViewHolder;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        NetworkModule.class,
        UtilsModule.class,
        DataStoreModule.class
})
public interface Graph {

    ActivityComponent plusActivity(ActivityModule activityModule);

    void inject(QuickBeer application);

    void inject(NetworkService networkService);

    void inject(BeerDetailsViewHolder view);

}
