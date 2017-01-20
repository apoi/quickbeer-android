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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import quickbeer.android.activities.base.SearchActivity;
import quickbeer.android.fragments.BeersInStyleFragment;
import timber.log.Timber;

public class BeersInStyleActivity extends SearchActivity {

    private String styleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            styleId = savedInstanceState.getString("id");
        } else {
            styleId = getIntent().getStringExtra("id");
        }

        activitySubscription.add(getQueryObservable()
                .subscribe(
                        query -> {
                            Timber.d("query(" + query + ")");

                            Intent intent = new Intent(this, BeerSearchActivity.class);
                            intent.putExtra("query", query);
                            startActivity(intent);
                        },
                        Timber::e));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("id", styleId);
    }

    @Override
    protected Fragment getFragment() {
        return new BeersInStyleFragment();
    }

    public String getStyleId() {
        return styleId;
    }
}
