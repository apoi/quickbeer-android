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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.activity.DrawerActivity;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.features.main.fragments.BeerListFragment;
import quickbeer.android.features.main.fragments.BeerSearchFragment;
import quickbeer.android.features.main.fragments.MainFragment;
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
    SearchViewViewModel searchViewViewModel;

    @Nullable
    @Inject
    MainActivityViewModel mainActivityViewModel;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull final CompositeSubscription s) {
            s.add(get(searchViewViewModel).getQueryStream()
                    .doOnNext(query -> Timber.d("query(" + query + ")"))
                    .subscribe(query -> tiggerSearch(query),
                            Timber::e));

            s.add(get(searchViewViewModel).getSearchQueriesOnceAndStream()
                    .doOnNext(list -> Timber.d("searches(" + list.size() + ")"))
                    .subscribe(query -> get(searchView).updateQueryList(query),
                            Timber::e));

            s.add(viewModel().searchHintStream()
                    .subscribe(value -> get(searchViewViewModel).setSearchHint(value),
                            Timber::e));
        }
    };

    private void tiggerSearch(@NonNull final String query) {
        Bundle bundle = new Bundle();
        bundle.putString("query", query);

        Fragment fragment = new BeerSearchFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack("search")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

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
                .ifNone(() -> {
                    getIntent().getStringExtra("query");
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, new MainFragment())
                            .commit();
                });
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @NonNull
    @Override
    protected MainActivityViewModel viewModel() {
        return get(mainActivityViewModel);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected");

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Timber.d("onBackPressed");

        checkNotNull(searchView);

        if (searchView.isSearchViewOpen()) {
            searchView.closeSearchView();
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }
}
