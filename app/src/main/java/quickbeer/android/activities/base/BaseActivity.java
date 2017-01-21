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
package quickbeer.android.activities.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import quickbeer.android.QuickBeer;
import quickbeer.android.R;
import quickbeer.android.activities.CountryListActivity;
import quickbeer.android.activities.MainActivity;
import quickbeer.android.activities.StyleListActivity;
import quickbeer.android.activities.TickedBeersActivity;
import quickbeer.android.activities.TopBeersActivity;
import quickbeer.android.injections.ActivityComponent;
import quickbeer.android.injections.ActivityModule;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Nullable
    protected ActivityComponent component;

    // Composite for subscriptions meant to stay alive for the activity lifetime
    protected final CompositeSubscription activitySubscription = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inject();
    }

    @Override
    protected void onDestroy() {
        activitySubscription.clear();

        super.onDestroy();
    }

    @NonNull
    public ActivityComponent getComponent() {
        if (component == null) {
            component = ((QuickBeer) getApplication()).graph()
                    .plusActivity(new ActivityModule(this));
        }

        return component;
    }

    protected abstract void inject();

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_main:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.nav_ticks:
                startActivity(new Intent(this, TickedBeersActivity.class));
                break;
            case R.id.nav_best:
                startActivity(new Intent(this, TopBeersActivity.class));
                break;
            case R.id.nav_countries:
                startActivity(new Intent(this, CountryListActivity.class));
                break;
            case R.id.nav_styles:
                startActivity(new Intent(this, StyleListActivity.class));
                break;
            case R.id.nav_about:
            default:
                return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
