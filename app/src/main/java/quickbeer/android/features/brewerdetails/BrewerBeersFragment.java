/**
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
package quickbeer.android.features.brewerdetails;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.features.list.fragments.BeerListFragment;
import quickbeer.android.viewmodels.BeerListViewModel;

import static io.reark.reark.utils.Preconditions.get;

public class BrewerBeersFragment  extends BeerListFragment {

    @Nullable
    @Inject
    BrewerBeersViewModel brewerBeersViewModel;

    private int brewerId;

    public static Fragment newInstance(int brewerId) {
        BrewerBeersFragment fragment = new BrewerBeersFragment();
        //noinspection AccessingNonPublicFieldOfAnotherObject
        fragment.brewerId = brewerId;
        return fragment;
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.brewer_details_fragment_beers;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            brewerId = savedInstanceState.getInt("brewerId");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        get(brewerBeersViewModel).setBrewerId(brewerId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("brewerId", brewerId);
    }

    @NonNull
    @Override
    protected BeerListViewModel viewModel() {
        return get(brewerBeersViewModel);
    }

    @Override
    protected void onQuery(@NonNull String query) {
        // Doesn't react to search view.
    }

}
