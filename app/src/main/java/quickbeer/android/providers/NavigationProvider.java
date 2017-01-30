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
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import javax.inject.Inject;
import javax.inject.Named;

import quickbeer.android.R;
import quickbeer.android.features.main.fragments.BeerSearchFragment;
import quickbeer.android.features.main.fragments.MainFragment;
import quickbeer.android.features.main.fragments.TopBeersFragment;
import rx.Observable;
import rx.subjects.PublishSubject;

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

    @NonNull
    private final PublishSubject<Page> pageSubject = PublishSubject.create();

    @Inject
    NavigationProvider(@NonNull final AppCompatActivity activity,
                       @NonNull @Named("fragmentContainer") final Integer container) {
        this.activity = get(activity);
        this.container = container;
    }

    @NonNull
    public Observable<Page> navigationStream() {
        return pageSubject.asObservable()
                .distinctUntilChanged();
    }

    public void navigateTo(@NonNull final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_main:
                navigateTo(Page.HOME);
                break;
            case R.id.nav_ticks:
                //startActivity(new Intent(this, TickedBeersActivity.class));
                break;
            case R.id.nav_best:
                navigateTo(Page.TOP_BEERS);
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

    public void navigateTo(@NonNull final Page page) {
        navigateTo(page, null);
    }

    public void navigateTo(@NonNull final Page page, @Nullable final Bundle arguments) {
        checkNotNull(page);

        final Fragment fragment = toFragment(page);
        fragment.setArguments(arguments);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(page.toString())
                .add(container, fragment)
                .commit();

        pageSubject.onNext(page);
    }

    public boolean canNavigateBack() {
        return activity.getSupportFragmentManager().getBackStackEntryCount() > 0;
    }

    public void navigateBack() {
        activity.getSupportFragmentManager().popBackStackImmediate();
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
