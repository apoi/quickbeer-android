/*
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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
import quickbeer.android.features.beerdetails.BeerDetailsFragment;
import quickbeer.android.features.beerdetails.BeerReviewsFragment;
import quickbeer.android.features.brewerdetails.BrewerBeersFragment;
import quickbeer.android.features.brewerdetails.BrewerDetailsFragment;
import quickbeer.android.features.countrydetails.CountryDetailsBeersFragment;
import quickbeer.android.features.countrydetails.CountryDetailsFragment;
import quickbeer.android.features.list.fragments.BeerSearchFragment;
import quickbeer.android.features.styledetails.StyleDetailsBeersFragment;
import quickbeer.android.features.styledetails.StyleDetailsFragment;

@Subcomponent(modules = SearchModule.class)
public interface SearchComponent {

    void inject(BeerSearchFragment beerSearchFragment);

}
