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
package quickbeer.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import quickbeer.android.activities.base.SearchBarActivity;
import quickbeer.android.data.DataLayer;
import timber.log.Timber;

public class BeerSearchFragment extends BeerListFragment {

    @Inject
    DataLayer.GetBeerSearch getBeerSearch;

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((SearchBarActivity) getActivity())
                .getQueryObservable()
                .doOnNext(query -> Timber.d("query(" + query + ")"))
                .subscribe(query -> setProgressingSource(getBeerSearch.call(query)), Timber::e);
    }
}
