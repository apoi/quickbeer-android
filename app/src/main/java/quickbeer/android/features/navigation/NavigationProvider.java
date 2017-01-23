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
package quickbeer.android.features.navigation;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import quickbeer.android.features.main.fragments.BeerSearchFragment;
import quickbeer.android.features.main.fragments.MainFragment;

import static io.reark.reark.utils.Preconditions.checkNotNull;

public final class NavigationProvider {

    public static void navigateTo(@NonNull final FragmentActivity activity,
                                  @NonNull final NavigationViewModel.Page page,
                                  @IdRes final int container) {
        checkNotNull(activity);
        checkNotNull(page);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(page.toString())
                .add(container, toFragment(page))
                .commit();
    }

    private static Fragment toFragment(@NonNull final NavigationViewModel.Page page) {
        switch (page) {
            case HOME:
                return new MainFragment();
            case BEER_SEARCH:
                return new BeerSearchFragment();
        }

        return new MainFragment();
    }

}
