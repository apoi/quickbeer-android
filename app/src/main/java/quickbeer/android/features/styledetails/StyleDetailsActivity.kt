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
package quickbeer.android.features.styledetails

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.collapsing_toolbar_activity.*
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.analytics.Events
import quickbeer.android.core.activity.BindingDrawerActivity
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.data.actions.StyleActions
import quickbeer.android.data.pojos.BeerStyle
import quickbeer.android.providers.NavigationProvider
import quickbeer.android.providers.ProgressStatusProvider
import quickbeer.android.utils.kotlin.filterToValue
import quickbeer.android.utils.kotlin.isNumeric
import quickbeer.android.viewmodels.SearchViewViewModel
import timber.log.Timber
import javax.inject.Inject

class StyleDetailsActivity : BindingDrawerActivity() {

    @Inject
    internal lateinit var styleActions: StyleActions

    @Inject
    internal lateinit var searchViewViewModel: SearchViewViewModel

    @Inject
    internal lateinit var navigationProvider: NavigationProvider

    @Inject
    internal lateinit var progressStatusProvider: ProgressStatusProvider

    @Inject
    internal lateinit var analytics: Analytics

    private var styleId: Int = 0

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(disposable: CompositeDisposable) {
            // Set toolbar title
            disposable.add(
                styleActions.get(styleId)
                    .toObservable()
                    .filterToValue()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ setToolbarDetails(it) }, { Timber.e(it) }))

            disposable.add(
                progressStatusProvider
                    .progressStatus()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ progress_indicator_bar.setProgress(it) }, { Timber.e(it) }))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.collapsing_toolbar_activity)
        setupDrawerLayout(false)
        setBackNavigationEnabled(true)

        if (savedInstanceState != null) {
            styleId = savedInstanceState.getInt(Constants.ID_KEY)
        } else {
            if (Intent.ACTION_VIEW == intent.action) {
                val idSegment = intent.data.pathSegments.find { it.isNumeric() }
                if (idSegment != null) {
                    styleId = idSegment.toInt()
                    analytics.createEvent(Events.Entry.LINK_STYLE)
                }
            }

            if (styleId <= 0) {
                styleId = intent.getIntExtra(Constants.ID_KEY, 0)
            }

            val defaultIndex = intent.getIntExtra(Constants.PAGER_INDEX, 0)

            supportFragmentManager.beginTransaction()
                .add(R.id.container, StyleDetailsPagerFragment.newInstance(styleId, defaultIndex))
                .commit()
        }
    }

    private fun setToolbarDetails(style: BeerStyle) {
        collapsing_toolbar.title = style.name
    }

    override fun inject() {
        getComponent().inject(this)
    }

    override fun viewModel(): SearchViewViewModel {
        return searchViewViewModel
    }

    override fun dataBinder(): DataBinder {
        return dataBinder
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(Constants.ID_KEY, styleId)
        super.onSaveInstanceState(outState)
    }

    override fun navigateTo(menuItem: MenuItem) {
        navigationProvider.navigateWithNewActivity(menuItem)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        Timber.d("onBackPressed")

        if (navigationProvider.canNavigateBack()) {
            navigationProvider.navigateBack()
        } else {
            super.onBackPressed()
        }
    }
}
