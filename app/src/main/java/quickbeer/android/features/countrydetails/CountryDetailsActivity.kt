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
package quickbeer.android.features.countrydetails

import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.reark.reark.utils.Preconditions.get
import kotlinx.android.synthetic.main.collapsing_toolbar_activity.*
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.core.activity.BindingDrawerActivity
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.data.DataLayer
import quickbeer.android.data.pojos.Country
import quickbeer.android.providers.NavigationProvider
import quickbeer.android.providers.ProgressStatusProvider
import quickbeer.android.rx.RxUtils
import quickbeer.android.utils.glide.SvgSoftwareLayerSetter
import quickbeer.android.viewmodels.SearchViewViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import javax.inject.Inject

class CountryDetailsActivity : BindingDrawerActivity() {

    @Inject
    lateinit var getCountry: DataLayer.GetCountry

    @Inject
    lateinit var searchViewViewModel: SearchViewViewModel

    @Inject
    lateinit var navigationProvider: NavigationProvider

    @Inject
    lateinit var progressStatusProvider: ProgressStatusProvider

    @Inject
    lateinit var analytics: Analytics

    private var countryId: Int = 0

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(subscription: CompositeSubscription) {
            // Set toolbar title
            subscription.add(getCountry.call(countryId)
                    .toObservable()
                    .compose({ RxUtils.pickValue(it) })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ setToolbarDetails(it) }, { Timber.e(it) }))

            subscription.add(get(progressStatusProvider)
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
            countryId = savedInstanceState.getInt(Constants.ID_KEY)
        } else {
            if (countryId <= 0) {
                countryId = intent.getIntExtra(Constants.ID_KEY, 0)
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.container, CountryDetailsPagerFragment.newInstance(countryId))
                    .commit()
        }
    }

    private fun setToolbarDetails(country: Country) {
        collapsing_toolbar.title = country.name
        toolbar_overlay_gradient.visibility = View.VISIBLE

        Glide.with(this)
                .`as`(PictureDrawable::class.java)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(SvgSoftwareLayerSetter())
                .load(String.format(Constants.FLAG_IMAGE_PATH, country.refer.toLowerCase()))
                .into(collapsing_toolbar_background)
    }

    override fun inject() {
        component.inject(this)
    }

    override fun viewModel(): SearchViewViewModel {
        return searchViewViewModel
    }

    override fun dataBinder(): DataBinder {
        return dataBinder
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(Constants.ID_KEY, countryId)
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
