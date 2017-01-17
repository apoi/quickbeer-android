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
import android.view.View;

import javax.inject.Inject;

import io.reark.reark.utils.Log;
import quickbeer.android.R;
import quickbeer.android.data.DataLayer;
import quickbeer.android.rx.RxUtils;

public class TickedBeersFragment extends BeerListFragment {
    private static final String TAG = TickedBeersFragment.class.getSimpleName();

    @Inject
    DataLayer.GetTickedBeers getTickedBeers;

    @Inject
    DataLayer.GetUserSettings getUserSettings;

    @Override
    public int getLayout() {
        return R.layout.beer_list_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGraph().inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getUserSettings.call()
                       .compose(RxUtils::pickValue)
                       .filter(userSettings -> userSettings.isLogged() && !userSettings.getUserId().isEmpty())
                       .subscribe(userSettings -> setProgressingSource(getTickedBeers.call(userSettings.getUserId())),
                                  error -> Log.e(TAG, "Error getting settings", error));

    }
}
