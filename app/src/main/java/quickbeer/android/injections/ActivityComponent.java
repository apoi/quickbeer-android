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
import quickbeer.android.core.activity.InjectingDrawerActivity;
import quickbeer.android.features.about.AboutActivity;
import quickbeer.android.features.beerdetails.BeerDetailsActivity;
import quickbeer.android.features.beerdetails.BeerDetailsView;
import quickbeer.android.features.beerdetails.BeerReviewsViewHolder;
import quickbeer.android.features.brewerdetails.BrewerDetailsActivity;
import quickbeer.android.features.brewerdetails.BrewerDetailsView;
import quickbeer.android.features.home.HomeActivity;
import quickbeer.android.features.list.ListActivity;
import quickbeer.android.features.photoview.PhotoViewActivity;
import quickbeer.android.features.profile.ProfileActivity;
import quickbeer.android.views.BrewerListView;

@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    FragmentComponent plusFragment(FragmentModule fragmentModule);

    void inject(HomeActivity homeActivity);

    void inject(ProfileActivity profileActivity);

    void inject(AboutActivity aboutActivity);

    void inject(ListActivity listActivity);

    void inject(InjectingDrawerActivity baseActivity);

    void inject(BeerDetailsActivity beerDetailsActivity);

    void inject(BrewerDetailsActivity brewerDetailsActivity);

    void inject(PhotoViewActivity photoViewActivity);

    // Views. TODO should be some other for injecting to views

    void inject(BeerDetailsView view);

    void inject(BeerReviewsViewHolder view);

    void inject(BrewerListView view);

    void inject(BrewerDetailsView view);

}
