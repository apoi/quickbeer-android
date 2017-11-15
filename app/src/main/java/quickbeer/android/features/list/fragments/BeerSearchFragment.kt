/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.features.list.fragments

import android.os.Bundle
import io.reark.reark.utils.Preconditions.get
import polanski.option.Option.ofObj
import quickbeer.android.injections.SearchModule
import quickbeer.android.providers.NavigationProvider
import quickbeer.android.viewmodels.BeerListViewModel
import quickbeer.android.viewmodels.BeerSearchViewModel
import timber.log.Timber
import javax.inject.Inject

class BeerSearchFragment : BeerListFragment() {

    @Inject
    internal lateinit var beerSearchViewModel: BeerSearchViewModel

    @Inject
    internal lateinit var navigationProvider: NavigationProvider

    private var initialQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ofObj(savedInstanceState ?: arguments)
                .map { state -> state.getString("query") }
                .ifSome { value -> initialQuery = value }
                .ifNone { Timber.w("Expected state for initializing!") }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("query", initialQuery)
        super.onSaveInstanceState(outState)
    }

    override fun inject() {
        super.inject()

        component.plusSearch(SearchModule(initialQuery))
                .inject(this)
    }

    override fun viewModel(): BeerListViewModel {
        return get(beerSearchViewModel)
    }

    override fun onQuery(query: String) {
        // No action, new search replaces old results.
    }
}
