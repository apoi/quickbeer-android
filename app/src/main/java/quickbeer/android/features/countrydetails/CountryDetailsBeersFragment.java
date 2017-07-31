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
package quickbeer.android.features.countrydetails;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.features.list.fragments.BeerListFragment;
import quickbeer.android.injections.IdModule;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

public class CountryDetailsBeersFragment extends BeerListFragment {

    @Nullable
    @Inject
    BeersInCountryViewModel beersInCountryViewModel;

    private int countryId;

    @NonNull
    public static CountryDetailsBeersFragment newInstance(int countryId) {
        Bundle bundle = new Bundle();
        bundle.putInt("countryId", countryId);
        CountryDetailsBeersFragment fragment = new CountryDetailsBeersFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = savedInstanceState != null
                ? savedInstanceState
                : getArguments();

        ofObj(bundle)
                .map(state -> state.getInt("countryId"))
                .ifSome(value -> countryId = value)
                .ifNone(() -> Timber.w("Expected state for initializing!"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("countryId", countryId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected int getLayout() {
        return R.layout.beer_list_fragment_paged;
    }

    @Override
    protected void inject() {
        super.inject();

        getComponent()
                .plusId(new IdModule(countryId))
                .inject(this);
    }

    @NonNull
    @Override
    protected BeersInCountryViewModel viewModel() {
        return get(beersInCountryViewModel);
    }

    @Override
    protected void onQuery(@NonNull String query) {
        // No action, new search replaces old results.
    }
}
