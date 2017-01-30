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
package quickbeer.android.features.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.core.viewmodel.SimpleViewModel;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.NavigationProvider.Page;
import quickbeer.android.providers.ResourceProvider;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class MainActivityViewModel extends SimpleViewModel {

    @NonNull
    private final NavigationProvider navigationProvider;

    @NonNull
    private final PublishSubject<String> searchHintSubject = PublishSubject.create();

    @NonNull
    private final ResourceProvider resourceProvider;

    @Inject
    MainActivityViewModel(@NonNull final NavigationProvider navigationProvider,
                          @NonNull final ResourceProvider resourceProvider) {
        this.navigationProvider = get(navigationProvider);
        this.resourceProvider = get(resourceProvider);
    }

    @Override
    protected void bind(@NonNull CompositeSubscription subscription) {
        subscription.add(navigationProvider.navigationStream()
                .subscribe(this::updateSearchHint, Timber::e));
    }

    @NonNull
    public Observable<String> searchHintStream() {
        return searchHintSubject.asObservable();
    }

    public void navigateTo(@NonNull final MenuItem menuItem) {
        navigationProvider.navigateTo(menuItem);
    }

    public void navigateTo(@NonNull final Page page,
                              @Nullable final Bundle arguments) {
        navigationProvider.navigateTo(page, arguments);
    }

    private void updateSearchHint(@NonNull final Page page) {
        switch (page) {
            case HOME:
            case BEER_SEARCH:
            case TOP_BEERS:
                searchHintSubject.onNext(resourceProvider.getString(R.string.search_box_hint_search_beers));
                break;
        }
    }

}
