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
package quickbeer.android.core.activity;

import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;

import quickbeer.android.Constants;
import quickbeer.android.R;

import static io.reark.reark.utils.Preconditions.get;

public abstract class DrawerActivity extends BindingBaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle drawerToggle;

    private DrawerLayout drawerLayout;

    private boolean backNavigationEnabled;

    protected void setupDrawerLayout() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, (Toolbar) findViewById(R.id.toolbar),
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        ActionBar actionBar = get(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        drawerToggle.syncState();

        toolbar.setNavigationOnClickListener(v -> {
            if (backNavigationEnabled) {
                onBackPressed();
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        navigateTo(item);

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    protected void setBackNavigationEnabled(boolean enabled) {
        if (backNavigationEnabled == enabled) {
            return;
        }

        backNavigationEnabled = enabled;

        if (enabled) {
            enableBackNavigation();
        } else {
            disableBackNavigation();
        }
    }

    private void enableBackNavigation() {
        ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
        anim.addUpdateListener(valueAnimator -> {
            float slideOffset = (Float) valueAnimator.getAnimatedValue();
            drawerToggle.onDrawerSlide(drawerLayout, slideOffset);
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(Constants.NAV_ARROW_ANIMATION_DURATION);
        anim.start();

        drawerLayout.removeDrawerListener(drawerToggle);
    }

    private void disableBackNavigation() {
        ValueAnimator anim = ValueAnimator.ofFloat(1.0f, 0.0f);
        anim.addUpdateListener(valueAnimator -> {
            float slideOffset = (Float) valueAnimator.getAnimatedValue();
            drawerToggle.onDrawerSlide(drawerLayout, slideOffset);
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(Constants.NAV_ARROW_ANIMATION_DURATION);
        anim.start();

        drawerLayout.addDrawerListener(drawerToggle);
    }

    protected abstract void navigateTo(@NonNull final MenuItem item);

}
