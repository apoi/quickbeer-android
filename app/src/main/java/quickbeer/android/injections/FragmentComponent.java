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
import quickbeer.android.features.beer.BeerDetailsFragment;
import quickbeer.android.features.main.fragments.BeerSearchFragment;
import quickbeer.android.features.main.fragments.BeerTabFragment;
import quickbeer.android.features.main.fragments.BeersInCountryFragment;
import quickbeer.android.features.main.fragments.BeersInStyleFragment;
import quickbeer.android.features.main.fragments.CountryListFragment;
import quickbeer.android.features.main.fragments.MainFragment;
import quickbeer.android.features.main.fragments.StyleListFragment;
import quickbeer.android.features.main.fragments.TickedBeersFragment;
import quickbeer.android.features.main.fragments.TopBeersFragment;

@FragmentScope
@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent {

    void inject(MainFragment mainFragment);

    void inject(BeerTabFragment beerTabFragment);

    void inject(BeerSearchFragment beerSearchFragment);

    void inject(BeerDetailsFragment beerDetailsFragment);

    void inject(TickedBeersFragment tickedBeersFragment);

    void inject(TopBeersFragment topBeersFragment);

    void inject(BeersInCountryFragment beersInCountryFragment);

    void inject(BeersInStyleFragment beersInStyleFragment);

    void inject(CountryListFragment countryListFragment);

    void inject(StyleListFragment styleListFragment);

}
