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
package quickbeer.android.features.home

import android.view.MenuItem
import quickbeer.android.R
import quickbeer.android.features.list.ListActivity
import quickbeer.android.providers.NavigationProvider
import quickbeer.android.providers.NavigationProvider.Page
import javax.inject.Inject

class HomeActivity : ListActivity() {

    @Inject
    internal lateinit var navigationProvider: NavigationProvider

    override fun defaultPage(): Page {
        return Page.HOME
    }

    override fun initialBackNavigationEnabled(): Boolean {
        supportFragmentManager.addOnBackStackChangedListener { setBackNavigationEnabled(navigationProvider.canNavigateBack()) }

        return false
    }

    override fun inject() {
        getComponent().inject(this)
    }

    override fun navigateTo(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.nav_home) {
            navigationProvider.navigateAllBack()
        } else {
            super.navigateTo(menuItem)
        }
    }
}
