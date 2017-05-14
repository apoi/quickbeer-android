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
package quickbeer.android.features.list.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import quickbeer.android.data.stores.BeerStyleStore;
import quickbeer.android.injections.IdModule;
import quickbeer.android.viewmodels.BeerListViewModel;
import quickbeer.android.viewmodels.BeersInStyleViewModel;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

public class BeersInStyleFragment  extends BeerListFragment {

    @Nullable
    @Inject
    BeersInStyleViewModel beersInStyleViewModel;

    @Nullable
    @Inject
    BeerStyleStore beerStyleStore;

    private int styleId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = savedInstanceState != null
                ? savedInstanceState
                : getArguments();

        ofObj(bundle)
                .map(state -> state.getInt("styleId", -1))
                .ifSome(value -> styleId = value)
                .ifNone(() -> Timber.w("Expected state for initializing!"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("styleId", styleId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void inject() {
        super.inject();

        getComponent()
                .plusId(new IdModule(styleId))
                .inject(this);
    }

    @NonNull
    @Override
    protected BeerListViewModel viewModel() {
        return get(beersInStyleViewModel);
    }

    @Override
    protected void onQuery(@NonNull String query) {
        // No action, new search replaces old results.
    }
}
