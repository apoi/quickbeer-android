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
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import quickbeer.android.next.R;
import quickbeer.android.next.data.DataLayer;
import rx.Observable;

public abstract class SearchActivity extends SearchBarActivity {
    private String query;

    @Inject
    DataLayer.GetBeerSearchQueries getBeerSearchQueries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            query = savedInstanceState.getString("query");
        } else {
            query = getIntent().getStringExtra("query");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void inject() {
        super.inject();
        getGraph().inject(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", query);
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    protected Observable<List<String>> getInitialQueriesObservable() {
        return getBeerSearchQueries.call();
    }

    @Override
    protected String getSearchHint() {
        return getString(R.string.search_box_hint_search_beers);
    }

    @Override
    protected boolean liveFilteringEnabled() {
        return false;
    }

    @Override
    protected boolean contentOverlayEnabled() {
        return true;
    }

    @Override
    protected int minimumSearchLength() {
        return 3;
    }

    @Override
    protected void showTooShortSearchError() {
        Toast.makeText(this, R.string.search_too_short, Toast.LENGTH_SHORT).show();
    }
}
