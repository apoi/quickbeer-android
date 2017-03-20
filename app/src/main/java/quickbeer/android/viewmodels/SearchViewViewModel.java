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

import java.util.Collections;
import java.util.List;

import polanski.option.Option;
import quickbeer.android.core.viewmodel.SimpleViewModel;
import quickbeer.android.data.DataLayer;
import quickbeer.android.rx.RxUtils;
import quickbeer.android.rx.Unit;
import rx.Observable;
import rx.subjects.PublishSubject;

import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.none;
import static polanski.option.Option.ofObj;

public class SearchViewViewModel extends SimpleViewModel {

    public enum Mode {
        SEARCH,
        FILTER
    }

    @NonNull
    private final DataLayer.GetBeerSearchQueries getBeerSearchQueries;

    private boolean liveFilteringEnabled;

    private boolean conventOverlayEnabled = true;

    private int minimumSearchLength = 4;

    @NonNull
    private String searchHint = "Search beers";

    @NonNull
    private Option<String> lastQuery = none();

    @NonNull
    private final PublishSubject<Option<String>> querySubject = PublishSubject.create();

    @NonNull
    private final PublishSubject<Unit> modeChangedSubject = PublishSubject.create();

    public SearchViewViewModel(@NonNull DataLayer.GetBeerSearchQueries getBeerSearchQueries) {
        this.getBeerSearchQueries = get(getBeerSearchQueries);
    }

    public void setQuery(@Nullable String query) {
        lastQuery = ofObj(query);
        querySubject.onNext(lastQuery);
    }

    @NonNull
    public Observable<List<String>> getSearchQueriesOnceAndStream() {
        return modeChangedSubject.asObservable()
                .map(__ -> liveFilteringEnabled)
                .distinctUntilChanged()
                .switchMap(live -> live
                        ? Observable.concat(
                            Observable.just(Collections.emptyList()),
                            Observable.never())
                        : oldSearchesObservable());
    }

    @NonNull
    private Observable<List<String>> oldSearchesObservable() {
        Observable<List<String>> oldQueries = getBeerSearchQueries.call().share();

        return Observable.combineLatest(
                oldQueries,
                searchQueries(),
                (List<String> list, String query) -> {
                    list.add(query);
                    return list;
                })
                .startWith(oldQueries);
    }

    @NonNull
    private Observable<String> searchQueries() {
        return getQueryStream()
                .filter(__ -> !liveFilteringEnabled)
                .distinctUntilChanged();
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

    public void setMode(@NonNull Mode mode, @NonNull String searchHint) {
        if (mode == Mode.SEARCH) {
            liveFilteringEnabled = false;
            conventOverlayEnabled = true;
            minimumSearchLength = 4;
        } else {
            liveFilteringEnabled = true;
            conventOverlayEnabled = false;
            minimumSearchLength = -1;
        }

        this.searchHint = get(searchHint);
        lastQuery = Option.none();

        modeChangedSubject.onNext(Unit.DEFAULT);
    }

    @NonNull
    public Observable<Unit> modeChangedStream() {
        return modeChangedSubject.asObservable();
    }

    public boolean liveFilteringEnabled() {
        return liveFilteringEnabled;
    }

    public boolean contentOverlayEnabled() {
        return conventOverlayEnabled;
    }

    public int minimumSearchLength() {
        return minimumSearchLength;
    }

    @NonNull
    public String getSearchHint() {
        return searchHint;
    }

}
