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
package quickbeer.android.providers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import javax.inject.Inject;
import javax.inject.Named;

import quickbeer.android.R;
import quickbeer.android.features.main.fragments.BeerSearchFragment;
import quickbeer.android.features.main.fragments.MainFragment;
import quickbeer.android.features.main.fragments.TopBeersFragment;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public final class NavigationProvider {

    public enum Page {
        HOME,
        BEER_SEARCH,
        TOP_BEERS;
    }

    @NonNull
    private final AppCompatActivity activity;

    @NonNull
    private final Integer container;

    @Inject
    NavigationProvider(@NonNull final AppCompatActivity activity,
                       @NonNull @Named("fragmentContainer") final Integer container) {
        this.activity = get(activity);
        this.container = container;
    }

    public void addPage(@NonNull final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_main:
                addPage(Page.HOME);
                break;
            case R.id.nav_ticks:
                //startActivity(new Intent(this, TickedBeersActivity.class));
                break;
            case R.id.nav_best:
                addPage(Page.TOP_BEERS);
                break;
            case R.id.nav_countries:
                //startActivity(new Intent(this, CountryListActivity.class));
                break;
            case R.id.nav_styles:
                //startActivity(new Intent(this, StyleListActivity.class));
                break;
            case R.id.nav_about:
            default:
                break;
        }
    }

    public void addPage(@NonNull final Page page) {
        transaction(page, null, true);
    }

    public void addPage(@NonNull final Page page, @Nullable final Bundle arguments) {
        transaction(page, arguments, true);
    }

    public void replacePage(@NonNull final Page page) {
        transaction(page, null, false);
    }

    public void clearToPage(@NonNull final MenuItem menuItem) {
        activity.getSupportFragmentManager()
                .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        addPage(menuItem);
    }

    private void transaction(@NonNull final Page page, @Nullable final Bundle arguments, boolean addToBackStack) {
        checkNotNull(page);

        final Fragment fragment = toFragment(page);
        fragment.setArguments(arguments);

        FragmentTransaction transition = activity
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(container, fragment, page.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (addToBackStack) {
            transition = transition.addToBackStack(page.toString());
        }

        transition.commit();
    }

    public boolean canNavigateBack() {
        return activity.getSupportFragmentManager().getBackStackEntryCount() > 1;
    }

    public void navigateBack() {
        activity.getSupportFragmentManager().popBackStack();
    }

    public void triggerSearch(@NonNull final String query) {
        Timber.d("query(" + query + ")");

        Bundle bundle = new Bundle();
        bundle.putString("query", query);

        addPage(Page.BEER_SEARCH, bundle);
    }

    private static Fragment toFragment(@NonNull final Page page) {
        switch (page) {
            case HOME:
                return new MainFragment();
            case BEER_SEARCH:
                return new BeerSearchFragment();
            case TOP_BEERS:
                return new TopBeersFragment();
        }

        return new MainFragment();
    }

}
