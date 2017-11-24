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
package quickbeer.android.core.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import polanski.option.Option
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.features.profile.ProfileActivity

abstract class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val DRAWER_ACTION_DELAY = 250

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var drawerLayout: DrawerLayout

    private var backNavigationEnabled = true

    protected fun setupDrawerLayout(startWithDrawerOpen: Boolean) {
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        Option.ofObj(navigationView.getHeaderView(0))
                .ifSome { setHeaderClickListener(it) }

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        drawerToggle = ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar) as Toolbar,
                R.string.action_drawer_open,
                R.string.action_drawer_close)

        supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        backNavigationEnabled = initialBackNavigationEnabled()
        drawerToggle.syncState()
        drawerToggle.onDrawerSlide(drawerLayout, if (backNavigationEnabled) 1.0f else 0.0f)

        if (!backNavigationEnabled) {
            drawerLayout.addDrawerListener(drawerToggle)
        }

        toolbar.setNavigationOnClickListener {
            if (backNavigationEnabled) {
                onBackPressed()
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        if (startWithDrawerOpen) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setHeaderClickListener(view: View) {
        view.setOnClickListener {
            drawerLayout.closeDrawers()
            drawerLayout.postDelayed({
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }, DRAWER_ACTION_DELAY.toLong())
        }
    }

    protected open fun initialBackNavigationEnabled(): Boolean {
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawers()
        drawerLayout.postDelayed({ navigateTo(item) }, DRAWER_ACTION_DELAY.toLong())
        return true
    }

    protected fun setBackNavigationEnabled(enabled: Boolean) {
        if (backNavigationEnabled != enabled) {
            backNavigationEnabled = enabled

            if (enabled) {
                enableBackNavigation()
            } else {
                disableBackNavigation()
            }
        }
    }

    private fun enableBackNavigation() {
        ValueAnimator.ofFloat(0.0f, 1.0f).apply {
            addUpdateListener { valueAnimator ->
                val slideOffset = valueAnimator.animatedValue as Float
                drawerToggle.onDrawerSlide(drawerLayout, slideOffset)
            }
            interpolator = DecelerateInterpolator()
            duration = Constants.NAV_ARROW_ANIMATION_DURATION.toLong()
            start()
        }

        drawerLayout.removeDrawerListener(drawerToggle)
    }

    private fun disableBackNavigation() {
        ValueAnimator.ofFloat(1.0f, 0.0f).apply {
            addUpdateListener { valueAnimator ->
                val slideOffset = valueAnimator.animatedValue as Float
                drawerToggle.onDrawerSlide(drawerLayout, slideOffset)
            }
            interpolator = DecelerateInterpolator()
            duration = Constants.NAV_ARROW_ANIMATION_DURATION.toLong()
            start()
        }

        drawerLayout.addDrawerListener(drawerToggle)
    }

    protected abstract fun navigateTo(menuItem: MenuItem)
}
