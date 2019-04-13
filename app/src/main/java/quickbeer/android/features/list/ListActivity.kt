/**
 * This file is part of QuickBeer.
 * Copyright (C) 2019 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.features.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.home_activity.*
import quickbeer.android.R
import quickbeer.android.core.activity.BindingDrawerActivity
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.features.about.AboutActivity
import quickbeer.android.providers.NavigationProvider
import quickbeer.android.providers.NavigationProvider.Page
import quickbeer.android.providers.PreferencesProvider
import quickbeer.android.providers.ProgressStatusProvider
import quickbeer.android.utils.kotlin.ifNull
import quickbeer.android.viewmodels.SearchViewViewModel
import quickbeer.android.views.ProgressIndicatorBar
import quickbeer.android.views.SearchView
import timber.log.Timber
import javax.inject.Inject

open class ListActivity : BindingDrawerActivity() {

    @Inject
    internal lateinit var navigationProvider: NavigationProvider

    @Inject
    internal lateinit var preferencesProvider: PreferencesProvider

    @Inject
    internal lateinit var searchViewViewModel: SearchViewViewModel

    @Inject
    internal lateinit var progressStatusProvider: ProgressStatusProvider

    private val searchView: SearchView by lazy { toolbar_search_view as SearchView }
    private val progressIndicatorBar: ProgressIndicatorBar by lazy { progress_indicator_bar }

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(disposable: CompositeDisposable) {
            viewModel()
                .getSearchQueriesOnceAndStream()
                .doOnNext { list -> Timber.d("searches(${list.size})") }
                .subscribe({ searchView.updateQueryList(it) }, Timber::e)
                .also { disposable.add(it) }

            viewModel()
                .modeChangedStream()
                .subscribe({ searchView.closeSearchView() }, Timber::e)
                .also { disposable.add(it) }

            progressStatusProvider.progressStatus()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ progressIndicatorBar.setProgress(it) }, Timber::e)
                .also { disposable.add(it) }
        }
    }

    override fun inject() {
        getComponent().inject(this)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.home_activity)

        // Drawer is shown on first run for making user aware of it
        setupDrawerLayout(!preferencesProvider.isFirstRunDrawerShown)
        preferencesProvider.setIsFirstRunDrawerShown(true)

        searchView.setViewModel(searchViewViewModel)

        savedInstanceState?.getString("query")?.let {
            searchViewViewModel.query = it
        }.ifNull {
            if (NavigationProvider.intentHasNavigationTarget(intent)) {
                navigationProvider.navigateWithIntent(intent)
            } else {
                navigationProvider.addPage(defaultPage())
            }
        }
    }

    override fun onResume() {
        searchView.closeSearchView()

        super.onResume()
    }

    protected open fun defaultPage(): Page {
        return Page.BEER_SEARCH
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (NavigationProvider.intentHasNavigationTarget(intent)) {
            navigationProvider.navigateWithIntent(intent)
        }
    }

    override fun viewModel(): SearchViewViewModel {
        return searchViewViewModel
    }

    override fun dataBinder(): DataBinder {
        return dataBinder
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        searchView.setMenuItem(menu.findItem(R.id.action_search))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun navigateTo(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.nav_about) {
            navigationProvider.launchActivity(AboutActivity::class.java)
        } else {
            navigationProvider.navigateWithCurrentActivity(menuItem)
        }
    }

    override fun onBackPressed() {
        Timber.d("onBackPressed")

        when {
            searchView.isSearchViewOpen() -> searchView.closeSearchView()
            navigationProvider.canNavigateBack() -> navigationProvider.navigateBack()
            else -> super.onBackPressed()
        }
    }
}
