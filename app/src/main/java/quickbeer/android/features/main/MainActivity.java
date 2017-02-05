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
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.core.activity.DrawerActivity;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.core.viewmodel.ViewModel;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.NavigationProvider.Page;
import quickbeer.android.viewmodels.SearchViewViewModel;
import quickbeer.android.views.SearchView;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

public class MainActivity extends DrawerActivity {

    @Nullable
    private SearchView searchView;

    @Nullable
    @Inject
    NavigationProvider navigationProvider;

    @Nullable
    @Inject
    SearchViewViewModel searchViewViewModel;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull final CompositeSubscription s) {
            s.add(viewModel().getSearchQueriesOnceAndStream()
                    .doOnNext(list -> Timber.d("searches(" + list.size() + ")"))
                    .subscribe(query -> get(searchView).updateQueryList(query),
                            Timber::e));
        }
    };

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        searchView = get((SearchView) findViewById(R.id.toolbar_search_view));
        searchView.setViewModel(get(searchViewViewModel));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = get(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        setupDrawerLayout();

        ofObj(savedInstanceState)
                .ifSome(state -> get(searchViewViewModel).setQuery(state.getString("query")))
                .ifNone(() -> get(navigationProvider).replacePage(Page.HOME));
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @NonNull
    @Override
    protected SearchViewViewModel viewModel() {
        return get(searchViewViewModel);
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        get(searchView).setMenuItem(menu.findItem(R.id.action_search));
        return true;
    }

    @Override
    protected void navigateTo(@NonNull final MenuItem menuItem) {
    }

    @Override
    public void onBackPressed() {
        Timber.d("onBackPressed");

        checkNotNull(searchView);
        checkNotNull(navigationProvider);

        if (searchView.isSearchViewOpen()) {
            searchView.closeSearchView();
        } else if (navigationProvider.canNavigateBack()) {
            navigationProvider.navigateBack();
        } else {
            super.onBackPressed();
        }
    }
}
