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
package quickbeer.android.features.navigation;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.core.viewmodel.SimpleViewModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class NavigationViewModel extends SimpleViewModel {

    public enum Page {
        HOME,
        BEER_SEARCH;
    }

    @NonNull
    private final AppCompatActivity activity;

    @NonNull
    private final NavigationProvider navigationProvider;

    @NonNull
    private final PublishSubject<Page> page = PublishSubject.create();

    @Inject
    NavigationViewModel(@NonNull final AppCompatActivity activity,
                        @NonNull final NavigationProvider navigationProvider) {
        this.activity = get(activity);
        this.navigationProvider = get(navigationProvider);
    }

    @NonNull
    public Observable<Page> navigationStream() {
        return page.asObservable()
                .distinctUntilChanged();
    }

    @Override
    protected void bind(@NonNull final CompositeSubscription subscription) {
        subscription.add(navigationStream()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(page -> Timber.d("Navigate to " + page))
                .subscribe(page -> NavigationProvider.navigateTo(activity, page, R.id.container),
                        Timber::e));
    }
}
