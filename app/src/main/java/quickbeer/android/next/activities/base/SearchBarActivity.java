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
package quickbeer.android.next.activities.base;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.miguelcatalan.materialsearchview.utils.AnimationUtil;

import java.util.List;

import io.reark.reark.utils.Log;
import quickbeer.android.next.R;
import quickbeer.android.next.adapters.SearchAdapter;
import quickbeer.android.next.rx.NullFilter;
import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class SearchBarActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = SearchBarActivity.class.getSimpleName();

    private SearchAdapter adapter;
    private MaterialSearchView searchView;
    private View searchViewOverlay;

    private PublishSubject<String> querySubject = PublishSubject.create();

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentViewLayout());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, getFragment())
                    .commit();
        }

        setupSearch();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        // There may have been queries while this fragment was paused. Refresh the search
        // history list adapter to have the latest queries available.
        adapter.refreshQueryList();
    }

    protected int getContentViewLayout() {
        return R.layout.main_activity;
    }

    public Observable<String> getQueryObservable() {
        return querySubject
                .asObservable()
                .filter(new NullFilter())
                .distinctUntilChanged();
    }

    private void setupSearch() {
        adapter = new SearchAdapter(this, getInitialQueriesObservable(), getQueryObservable());
        searchViewOverlay = findViewById(R.id.search_view_overlay);
        searchViewOverlay.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && searchView.isSearchOpen()) {
                searchView.closeSearch();
            }
            return true;
        });

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setHint(getSearchHint());
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit(" + query + ")");
                if (updateQueryText(query)) {
                    searchView.closeSearch();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.v(TAG, "onQueryTextChange(" + query + ")");
                if (liveFilteringEnabled()) {
                    updateQueryText(query);
                }
                return true;
            }

            private boolean updateQueryText(String query) {
                if (query.length() > minimumSearchLength()) {
                    querySubject.onNext(query);
                    return true;
                } else {
                    showTooShortSearchError();
                    return false;
                }
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override public void onSearchViewAboutToClose() {}

            @Override
            public void onSearchViewAboutToShow() {
                Log.d(TAG, "onSearchViewAboutToShow");

                getQueryObservable()
                        .first()
                        .subscribe(query -> searchView.setQuery(query, false));

                if (contentOverlayEnabled()) {
                    Animation fadeIn = new AlphaAnimation(0.0f, 0.5f);
                    fadeIn.setDuration(AnimationUtil.ANIMATION_DURATION_MEDIUM);
                    fadeIn.setFillAfter(true);

                    searchViewOverlay.setVisibility(View.VISIBLE);
                    searchViewOverlay.startAnimation(fadeIn);
                }
            }

            @Override
            public void onSearchViewShown() {
                Log.d(TAG, "onSearchViewShown");

                searchView.setAdapter(adapter);
            }

            @Override
            public void onSearchViewClosed() {
                Log.d(TAG, "onSearchViewClosed");

                searchView.setAdapter(null);
                searchViewOverlay.clearAnimation();
                searchViewOverlay.setVisibility(View.GONE);
            }
        });

        searchView.setOnItemClickListener((parent, view, position, id) -> {
            searchView.closeSearch();
            querySubject.onNext(adapter.getItem(position));
        });
    }

    public void closeSearch() {
        searchView.closeSearch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle drawerLayout actions
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected abstract Observable<List<String>> getInitialQueriesObservable();

    protected abstract String getSearchHint();

    protected abstract boolean liveFilteringEnabled();

    protected abstract boolean contentOverlayEnabled();

    protected abstract int minimumSearchLength();

    protected abstract void showTooShortSearchError();

    protected abstract Fragment getFragment();
}
