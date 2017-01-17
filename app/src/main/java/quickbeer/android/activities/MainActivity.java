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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.R;
import quickbeer.android.activities.base.SearchActivity;
import quickbeer.android.fragments.MainFragment;

public class MainActivity extends SearchActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        Preconditions.checkNotNull(drawerLayout, "DrawerLayout is required.");

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        activitySubscription.add(getQueryObservable()
                .subscribe(
                        query -> {
                            Log.d(TAG, "query(" + query + ")");

                            Intent intent = new Intent(this, BeerSearchActivity.class);
                            intent.putExtra("query", query);
                            startActivity(intent);
                        },
                        throwable -> {
                            Log.e(TAG, "error", throwable);
                        }));
    }

    @Override
    protected Fragment getFragment() {
        return new MainFragment();
    }
}
