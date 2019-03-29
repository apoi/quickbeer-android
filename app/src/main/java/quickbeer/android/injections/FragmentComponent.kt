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
import quickbeer.android.features.about.AboutDetailsFragment
import quickbeer.android.features.beerdetails.BeerDetailsPagerFragment
import quickbeer.android.features.brewerdetails.BrewerDetailsPagerFragment
import quickbeer.android.features.countrydetails.CountryDetailsPagerFragment
import quickbeer.android.features.home.BeerTabFragment
import quickbeer.android.features.home.BrewerTabFragment
import quickbeer.android.features.home.HomeFragment
import quickbeer.android.features.list.fragments.BarcodeSearchFragment
import quickbeer.android.features.list.fragments.BeerListFragment
import quickbeer.android.features.list.fragments.BrewerListFragment
import quickbeer.android.features.list.fragments.CountryListFragment
import quickbeer.android.features.list.fragments.StyleListFragment
import quickbeer.android.features.list.fragments.TickedBeersFragment
import quickbeer.android.features.list.fragments.TopBeersFragment
import quickbeer.android.features.profile.ProfileDetailsFragment
import quickbeer.android.features.profile.ProfileLoginFragment
import quickbeer.android.features.styledetails.StyleDetailsPagerFragment

@FragmentScope
@Subcomponent(modules = [FragmentModule::class])
interface FragmentComponent {

    fun plusId(idModule: IdModule): IdComponent

    fun plusSearch(searchModule: SearchModule): SearchComponent

    fun inject(homeFragment: HomeFragment)

    fun inject(profileLoginFragment: ProfileLoginFragment)

    fun inject(profileDetailsFragment: ProfileDetailsFragment)

    fun inject(beerTabFragment: BeerTabFragment)

    fun inject(brewerTabFragment: BrewerTabFragment)

    fun inject(beerListFragment: BeerListFragment)

    fun inject(brewerListFragment: BrewerListFragment)

    fun inject(barcodeSearchFragment: BarcodeSearchFragment)

    fun inject(beerDetailsPagerFragment: BeerDetailsPagerFragment)

    fun inject(brewerDetailsPagerFragment: BrewerDetailsPagerFragment)

    fun inject(tickedBeersFragment: TickedBeersFragment)

    fun inject(topBeersFragment: TopBeersFragment)

    fun inject(countryListFragment: CountryListFragment)

    fun inject(styleListFragment: StyleListFragment)

    fun inject(styleDetailsPagerFragment: StyleDetailsPagerFragment)

    fun inject(countryDetailsPagerFragment: CountryDetailsPagerFragment)

    fun inject(aboutDetailsFragment: AboutDetailsFragment)
}
