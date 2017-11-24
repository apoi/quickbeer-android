/*
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
package quickbeer.android.features.about

import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.basic_activity.*
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.analytics.Events
import quickbeer.android.core.activity.InjectingDrawerActivity
import quickbeer.android.providers.NavigationProvider
import quickbeer.android.providers.NavigationProvider.Page
import javax.inject.Inject

class AboutActivity : InjectingDrawerActivity() {

    @Inject
    internal lateinit var navigationProvider: NavigationProvider

    @Inject
    internal lateinit var analytics: Analytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.basic_activity)
        setupDrawerLayout(false)
        setBackNavigationEnabled(true)
        toolbar.title = getString(R.string.about)

        analytics.createEvent(Events.Screen.ABOUT)

        if (savedInstanceState == null) {
            navigationProvider.addPage(Page.ABOUT)
        }
    }

    override fun inject() {
        component.inject(this)
    }

    override fun navigateTo(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.nav_about) {
            return
        }

        navigationProvider.navigateWithNewActivity(menuItem)
    }
}
