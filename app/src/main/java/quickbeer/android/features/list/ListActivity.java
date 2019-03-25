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
package quickbeer.android.features.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import quickbeer.android.R;
import quickbeer.android.core.activity.BindingDrawerActivity;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.features.about.AboutActivity;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.NavigationProvider.Page;
import quickbeer.android.providers.PreferencesProvider;
import quickbeer.android.providers.ProgressStatusProvider;
import quickbeer.android.viewmodels.SearchViewViewModel;
import quickbeer.android.views.ProgressIndicatorBar;
import quickbeer.android.views.SearchView;
import timber.log.Timber;

import javax.inject.Inject;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class ListActivity extends BindingDrawerActivity {

    @BindView(R.id.toolbar_search_view)
    SearchView searchView;

    @BindView(R.id.progress_indicator_bar)
    ProgressIndicatorBar progressIndicatorBar;

    @Nullable
    @Inject
    NavigationProvider navigationProvider;

    @Nullable
    @Inject
    PreferencesProvider preferencesProvider;

    @Nullable
    @Inject
    SearchViewViewModel searchViewViewModel;

    @Nullable
    @Inject
    ProgressStatusProvider progressStatusProvider;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeDisposable disposable) {
            disposable.add(viewModel()
                    .getSearchQueriesOnceAndStream()
                    .doOnNext(list -> Timber.d("searches(" + list.size() + ")"))
                    .subscribe(query -> get(searchView).updateQueryList(query), Timber::e));

            disposable.add(viewModel()
                    .modeChangedStream()
                    .subscribe(__ -> get(searchView).updateOptions(), Timber::e));

            disposable.add(get(progressStatusProvider)
                    .progressStatus()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(progressIndicatorBar::setProgress, Timber::e));
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkNotNull(navigationProvider);
        checkNotNull(preferencesProvider);
        checkNotNull(searchViewViewModel);

        setContentView(R.layout.home_activity);
        // Drawer is shown on first run for making user aware of it
        setupDrawerLayout(!preferencesProvider.isFirstRunDrawerShown());
        preferencesProvider.setIsFirstRunDrawerShown(true);

        ButterKnife.bind(this);

        searchView = get((SearchView) findViewById(R.id.toolbar_search_view));
        searchView.setViewModel(get(searchViewViewModel));

        if (savedInstanceState != null && savedInstanceState.containsKey("query")) {
            searchViewViewModel.setQuery(savedInstanceState.getString("query"));
        } else if (NavigationProvider.intentHasNavigationTarget(getIntent())) {
            navigationProvider.navigateWithIntent(getIntent());
        } else {
            navigationProvider.addPage(defaultPage());
        }
    }

    @Override
    protected void onPause() {
        get(searchView).closeSearchView();

        super.onPause();
    }

    @NonNull
    protected Page defaultPage() {
        return Page.BEER_SEARCH;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        checkNotNull(navigationProvider);

        if (NavigationProvider.intentHasNavigationTarget(intent)) {
            navigationProvider.navigateWithIntent(intent);
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void navigateTo(@NonNull MenuItem menuItem) {
        checkNotNull(navigationProvider);

        if (menuItem.getItemId() == R.id.nav_about) {
            navigationProvider.launchActivity(AboutActivity.class);
        } else {
            navigationProvider.navigateWithCurrentActivity(menuItem);
        }
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
