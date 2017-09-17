/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela></antti.poikela>@iki.fi>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.viewmodels

import io.reark.reark.utils.Preconditions.get
import polanski.option.Option
import polanski.option.Option.none
import polanski.option.Option.ofObj
import quickbeer.android.core.viewmodel.SimpleViewModel
import quickbeer.android.data.actions.BeerSearchActions
import quickbeer.android.rx.RxUtils
import quickbeer.android.rx.Unit
import rx.Observable
import rx.subjects.PublishSubject

class SearchViewViewModel(private val beerSearchActions: BeerSearchActions)
    : SimpleViewModel() {

    enum class Mode {
        SEARCH,
        FILTER
    }

    private var liveFilteringEnabled: Boolean = false

    private var conventOverlayEnabled = true

    private var minimumSearchLength = 4

    var searchHint = "Search beers"
        private set

    private var lastQuery = none<String>()

    private val querySubject = PublishSubject.create<Option<String>>()

    private val modeChangedSubject = PublishSubject.create<Unit>()

    var query: String
        get() = lastQuery.orDefault { "" }
        set(query) {
            lastQuery = ofObj(query)
            querySubject.onNext(lastQuery)
        }

    fun getSearchQueriesOnceAndStream(): Observable<List<String>> {
        return modeChangedSubject.asObservable()
                .map { liveFilteringEnabled }
                .distinctUntilChanged()
                .switchMap { live ->
                    if (live)
                        Observable.concat(Observable.just(emptyList()), Observable.never())
                    else
                        oldSearchesObservable()
                }
    }

    private fun oldSearchesObservable(): Observable<List<String>> {
        val oldQueries = beerSearchActions.searchQueries().share()

        return Observable.combineLatest(oldQueries, searchQueries(),
                    { list: List<String>, query: String -> list.toMutableList().add(query); list })
                .startWith(oldQueries)
    }

    private fun searchQueries(): Observable<String> {
        return getQueryStream()
                .filter { !liveFilteringEnabled }
                .distinctUntilChanged()
    }

    fun getQueryStream(): Observable<String> {
        return querySubject
                .asObservable()
                .compose { RxUtils.pickValue(it) }
    }

    fun setMode(mode: Mode, searchHint: String) {
        if (mode == Mode.SEARCH) {
            liveFilteringEnabled = false
            conventOverlayEnabled = true
            minimumSearchLength = 4
        } else {
            liveFilteringEnabled = true
            conventOverlayEnabled = false
            minimumSearchLength = -1
        }

        this.searchHint = get(searchHint)
        lastQuery = Option.none()

        modeChangedSubject.onNext(Unit.DEFAULT)
    }

    fun modeChangedStream(): Observable<Unit> {
        return modeChangedSubject.asObservable()
    }

    fun liveFilteringEnabled(): Boolean {
        return liveFilteringEnabled
    }

    fun contentOverlayEnabled(): Boolean {
        return conventOverlayEnabled
    }

    fun minimumSearchLength(): Int {
        return minimumSearchLength
    }
}
