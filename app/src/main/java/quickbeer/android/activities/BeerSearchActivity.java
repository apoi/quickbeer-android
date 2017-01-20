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
package quickbeer.android.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import quickbeer.android.activities.base.SearchActivity;
import quickbeer.android.fragments.BeerSearchFragment;
import rx.Observable;

public class BeerSearchActivity extends SearchActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set title to reflect the search, update query
        activitySubscription.add(getQueryObservable()
                .doOnNext(this::setTitle)
                .doOnNext(this::setQuery)
                .subscribe());
    }

    @Override
    protected Fragment getFragment() {
        return new BeerSearchFragment();
    }

    @Override
    public Observable<String> getQueryObservable() {
        // Free search always starts with a submitted query
        return super.getQueryObservable()
                .startWith(getQuery());
    }
}
