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
package quickbeer.android.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.miguelcatalan.materialsearchview.utils.AnimationUtil;

import java.util.List;

import quickbeer.android.R;
import quickbeer.android.adapters.SearchAdapter;
import quickbeer.android.viewmodels.SearchViewViewModel;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class SearchView extends FrameLayout {

    @Nullable
    private SearchAdapter adapter;

    @Nullable
    private MaterialSearchView searchView;

    @Nullable
    private View searchViewOverlay;

    @Nullable
    private SearchViewViewModel viewModel;

    public SearchView(Context context) {
        super(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchViewOverlay = findViewById(R.id.search_view_overlay);
    }

    public void setViewModel(@NonNull SearchViewViewModel viewModel) {
        this.viewModel = get(viewModel);

        initialize();
    }

    @NonNull
    private SearchViewViewModel getViewModel() {
        return get(viewModel);
    }

    public void setMenuItem(@NonNull MenuItem menuItem) {
        get(searchView).setMenuItem(get(menuItem));
    }

    public boolean isSearchViewOpen() {
        return get(searchView).isSearchOpen();
    }

    public void closeSearchView() {
        get(searchView).closeSearch();
    }

    public void updateQueryList(@NonNull List<String> queries) {
        get(adapter).updateSourceList(queries);
    }

    private void initialize() {
        checkNotNull(searchView);
        checkNotNull(searchViewOverlay);

        adapter = new SearchAdapter(getContext());

        searchViewOverlay.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && searchView.isSearchOpen()) {
                searchView.closeSearch();
            }
            return true;
        });

        searchView.setHint(getViewModel().getSearchHint());
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Timber.d("onQueryTextSubmit(" + query + ")");
                if (updateQueryText(query)) {
                    searchView.closeSearch();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Timber.v("onQueryTextChange(" + query + ")");
                if (getViewModel().liveFilteringEnabled()) {
                    updateQueryText(query);
                }
                return true;
            }

            private boolean updateQueryText(String query) {
                if (query.length() >= getViewModel().minimumSearchLength()) {
                    getViewModel().setQuery(query);
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
                Timber.d("onSearchViewAboutToShow");

                searchView.setQuery(getViewModel().getQuery(), false);

                if (getViewModel().contentOverlayEnabled()) {
                    Animation fadeIn = new AlphaAnimation(0.0f, 0.5f);
                    fadeIn.setDuration(AnimationUtil.ANIMATION_DURATION_MEDIUM);
                    fadeIn.setFillAfter(true);

                    searchViewOverlay.setVisibility(View.VISIBLE);
                    searchViewOverlay.startAnimation(fadeIn);
                }
            }

            @Override
            public void onSearchViewShown() {
                Timber.d("onSearchViewShown");

                searchView.setAdapter(adapter);
            }

            @Override
            public void onSearchViewClosed() {
                Timber.d("onSearchViewClosed");

                searchView.setAdapter(null);
                searchViewOverlay.clearAnimation();
                searchViewOverlay.setVisibility(View.GONE);
            }
        });

        searchView.setOnItemClickListener((parent, view, position, id) -> {
            searchView.closeSearch();
            getViewModel().setQuery(adapter.getItem(position));
        });
    }

    public void showTooShortSearchError() {
        Toast.makeText(getContext(), R.string.search_too_short, Toast.LENGTH_SHORT).show();
    }

}
