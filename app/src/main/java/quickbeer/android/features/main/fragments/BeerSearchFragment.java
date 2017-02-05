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
package quickbeer.android.features.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.viewmodels.BeerListViewModel;
import quickbeer.android.viewmodels.BeerSearchViewModel;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

public class BeerSearchFragment extends BeerListFragment {

    @Nullable
    @Inject
    BeerSearchViewModel beerSearchViewModel;

    @Nullable
    @Inject
    NavigationProvider navigationProvider;

    @NonNull
    private String initialQuery = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ofObj(getArguments())
                .ifSome(state -> initialQuery = get(state.getString("query")))
                .ifNone(() -> Timber.w("Expected state for initializing!"));
    }

    @Override
    protected void inject() {
        super.inject();

        getComponent().inject(this);

        get(beerSearchViewModel).setInitialQuery(initialQuery);
    }

    @NonNull
    @Override
    protected BeerListViewModel viewModel() {
        return get(beerSearchViewModel);
    }

    @Override
    protected void onQuery(@NonNull final String query) {
        // No action, new search replaces old results.
    }
}
