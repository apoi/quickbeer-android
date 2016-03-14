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
package quickbeer.android.next.activities;

import java.util.List;

import quickbeer.android.next.R;
import rx.Observable;

public abstract class FilterActivity extends SearchBarActivity {
    @Override
    protected Observable<List<String>> getInitialQueriesObservable() {
        return Observable.empty();
    }

    @Override
    protected String getSearchHint() {
        return getString(R.string.country_activity_search_hint);
    }

    @Override
    protected boolean liveFiltering() {
        return true;
    }

    @Override
    protected int minimumSearchLength() {
        return -1;
    }

    @Override
    protected void showTooShortSearchError() {
    }
}
