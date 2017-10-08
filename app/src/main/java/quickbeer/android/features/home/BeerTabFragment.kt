/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.features.home

import io.reark.reark.utils.Preconditions.get
import quickbeer.android.R
import quickbeer.android.features.list.fragments.BeerListFragment
import quickbeer.android.viewmodels.BeerListViewModel
import quickbeer.android.viewmodels.NetworkViewModel.ProgressStatus
import quickbeer.android.viewmodels.RecentBeersViewModel
import javax.inject.Inject

class BeerTabFragment : BeerListFragment() {

    @Inject
    internal lateinit var recentBeersViewModel: RecentBeersViewModel

    override fun getLayout(): Int {
        return R.layout.beer_list_fragment_paged
    }

    override fun inject() {
        component.inject(this)
    }

    override fun viewModel(): BeerListViewModel {
        return get(recentBeersViewModel)
    }

    override fun onQuery(query: String) {
        // Doesn't react to search view.
    }

    override fun toStatusValue(progressStatus: ProgressStatus): String {
        return when (progressStatus) {
            ProgressStatus.EMPTY -> getString(R.string.home_empty_list)
            else -> super.toStatusValue(progressStatus)
        }
    }
}
