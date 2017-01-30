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
package quickbeer.android.viewmodels;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import polanski.option.Option;
import quickbeer.android.data.DataLayer;
import quickbeer.android.rx.RxUtils;
import rx.Observable;
import rx.subjects.PublishSubject;

import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.none;
import static polanski.option.Option.ofObj;

public class SearchViewViewModel {

    @Inject
    DataLayer.GetBeerSearchQueries getBeerSearchQueries;

    private boolean liveFilteringEnabled = false;

    private boolean conventOverlayEnabled = true;

    private int minimumSearchLength = 3;

    private String searchHint = "Search beers";

    @NonNull
    private Option<String> lastQuery = none();

    @NonNull
    private final PublishSubject<Option<String>> querySubject = PublishSubject.create();

    public SearchViewViewModel(@NonNull final DataLayer.GetBeerSearchQueries getBeerSearchQueries) {
        this.getBeerSearchQueries = get(getBeerSearchQueries);
    }

    public void setQuery(@Nullable final String query) {
        lastQuery = ofObj(query);
        querySubject.onNext(lastQuery);
    }

    @NonNull
    public Observable<List<String>> getSearchQueriesOnceAndStream() {
        Observable<List<String>> oldQueries = getBeerSearchQueries.call().share();

        return Observable.combineLatest(
                oldQueries,
                getQueryStream().distinctUntilChanged(),
                (List<String> list, String query) -> {
                    list.add(query);
                    return list;
                })
                .startWith(oldQueries);
    }

    @NonNull
    public Observable<String> getQueryStream() {
        return querySubject
                .asObservable()
                .compose(RxUtils::pickValue);
    }

    @NonNull
    public String getQuery() {
        return lastQuery
                .orDefault(() -> "");
    }

    public boolean liveFilteringEnabled() {
        return liveFilteringEnabled;
    }

    public void setLiveFilteringEnabled(boolean liveFilteringEnabled) {
        this.liveFilteringEnabled = liveFilteringEnabled;
    }

    public boolean contentOverlayEnabled() {
        return conventOverlayEnabled;
    }

    public void setConventOverlayEnabled(boolean conventOverlayEnabled) {
        this.conventOverlayEnabled = conventOverlayEnabled;
    }

    public String getSearchHint() {
        return searchHint;
    }

    public void setSearchHint(String searchHint) {
        this.searchHint = searchHint;
    }

    public int minimumSearchLength() {
        return minimumSearchLength;
    }

    public void setMinimumSearchLength(int minimumSearchLength) {
        this.minimumSearchLength = minimumSearchLength;
    }
}
