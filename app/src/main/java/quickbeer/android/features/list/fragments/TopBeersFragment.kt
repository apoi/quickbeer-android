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

import quickbeer.android.providers.NavigationProvider
import quickbeer.android.viewmodels.BeerListViewModel
import quickbeer.android.viewmodels.TopBeersViewModel
import javax.inject.Inject

class TopBeersFragment : BeerListFragment() {

    @Inject
    internal lateinit var topBeersViewModel: TopBeersViewModel

    @Inject
    internal lateinit var navigationProvider: NavigationProvider

    override fun inject() {
        super.inject()

        component.inject(this)
    }

    override fun viewModel(): BeerListViewModel {
        return topBeersViewModel
    }

    override fun onQuery(query: String) {
        navigationProvider.triggerSearch(query)
    }
}
