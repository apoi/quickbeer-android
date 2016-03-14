/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package quickbeer.android.next.activities;

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

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import io.reark.reark.utils.Log;
import quickbeer.android.next.R;
import quickbeer.android.next.adapters.SearchAdapter;
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
    protected void onCreate(Bundle savedInstanceState) {
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
                .asObservable();
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
                if (liveFiltering()) {
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
            @Override
            public void onSearchViewShown() {
                Log.d(TAG, "onSearchViewShown");
                searchViewOverlay.setVisibility(View.VISIBLE);
                searchView.setAdapter(adapter);
            }

            @Override
            public void onSearchViewClosed() {
                Log.d(TAG, "onSearchViewClosed");
                searchView.setAdapter(null);
                searchViewOverlay.setVisibility(View.GONE);
            }
        });

        searchView.setOnItemClickListener((parent, view, position, id) -> {
            searchView.closeSearch();
            querySubject.onNext(adapter.getItem(position));
        });
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

    protected abstract boolean liveFiltering();

    protected abstract int minimumSearchLength();

    protected abstract void showTooShortSearchError();

    protected abstract Fragment getFragment();
}
