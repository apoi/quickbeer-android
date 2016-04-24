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
package quickbeer.android.next.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import quickbeer.android.next.R;
import quickbeer.android.next.fragments.BeerTabFragment;
import quickbeer.android.next.fragments.BrewerTabFragment;

public class MainViewAdapter extends FragmentPagerAdapter {
    private final Context context;
    
    public MainViewAdapter(FragmentManager manager, Context context) {
        super(manager);

        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BeerTabFragment();
            case 1:
            default:
                return new BrewerTabFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.tab_beers);
            case 1:
            default:
                return context.getString(R.string.tab_brewers);
        }
    }
}
