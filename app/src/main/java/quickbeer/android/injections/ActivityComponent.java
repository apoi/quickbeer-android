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

import dagger.Subcomponent;
import quickbeer.android.activity.base.SearchActivity;
import quickbeer.android.activity.base.SearchBarActivity;
import quickbeer.android.core.activity.InjectingDrawerActivity;
import quickbeer.android.features.beerdetails.BeerDetailsActivity;
import quickbeer.android.features.beerdetails.BeerDetailsViewHolder;
import quickbeer.android.features.home.HomeActivity;
import quickbeer.android.features.list.ListActivity;
import quickbeer.android.features.profile.ProfileActivity;
import quickbeer.android.features.photoview.PhotoViewActivity;

@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    FragmentComponent plusFragment(FragmentModule fragmentModule);

    void inject(HomeActivity homeActivity);

    void inject(ProfileActivity homeActivity);

    void inject(ListActivity listActivity);

    void inject(InjectingDrawerActivity baseActivity);

    void inject(SearchBarActivity searchBarActivity);

    void inject(SearchActivity searchActivity);

    void inject(BeerDetailsActivity beerDetailsActivity);

    void inject(PhotoViewActivity photoViewActivity);

    void inject(BeerDetailsViewHolder view);

}
