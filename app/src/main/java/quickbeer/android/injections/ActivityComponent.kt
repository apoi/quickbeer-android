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

import dagger.Subcomponent
import quickbeer.android.core.activity.InjectingDrawerActivity
import quickbeer.android.features.about.AboutActivity
import quickbeer.android.features.beerdetails.BeerDetailsActivity
import quickbeer.android.features.beerdetails.BeerDetailsView
import quickbeer.android.features.beerdetails.BeerReviewsViewHolder
import quickbeer.android.features.brewerdetails.BrewerDetailsActivity
import quickbeer.android.features.brewerdetails.BrewerDetailsView
import quickbeer.android.features.countrydetails.CountryDetailsActivity
import quickbeer.android.features.countrydetails.CountryDetailsView
import quickbeer.android.features.home.HomeActivity
import quickbeer.android.features.list.ListActivity
import quickbeer.android.features.photoview.PhotoViewActivity
import quickbeer.android.features.profile.ProfileActivity
import quickbeer.android.features.styledetails.StyleDetailsActivity
import quickbeer.android.features.styledetails.StyleDetailsView
import quickbeer.android.views.BrewerListView

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {

    fun plusFragment(fragmentModule: FragmentModule): FragmentComponent

    fun inject(homeActivity: HomeActivity)

    fun inject(profileActivity: ProfileActivity)

    fun inject(aboutActivity: AboutActivity)

    fun inject(listActivity: ListActivity)

    fun inject(baseActivity: InjectingDrawerActivity)

    fun inject(beerDetailsActivity: BeerDetailsActivity)

    fun inject(brewerDetailsActivity: BrewerDetailsActivity)

    fun inject(styleDetailsActivity: StyleDetailsActivity)

    fun inject(countryDetailsActivity: CountryDetailsActivity)

    fun inject(photoViewActivity: PhotoViewActivity)

    // Views. TODO should be some other for injecting to views

    fun inject(view: BeerDetailsView)

    fun inject(view: BeerReviewsViewHolder)

    fun inject(view: BrewerListView)

    fun inject(view: BrewerDetailsView)

    fun inject(view: StyleDetailsView)

    fun inject(view: CountryDetailsView)
}
