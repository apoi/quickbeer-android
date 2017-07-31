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

import android.content.Context
import android.support.annotation.StringRes
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.widget.Toast
import io.reark.reark.utils.Preconditions.get
import kotlinx.android.synthetic.main.country_details_fragment_details.view.*
import quickbeer.android.analytics.Analytics
import quickbeer.android.core.activity.InjectingDrawerActivity
import quickbeer.android.data.pojos.Country
import quickbeer.android.providers.ResourceProvider
import quickbeer.android.providers.ToastProvider
import javax.inject.Inject

/**
 * View holder for all the country details
 */
class CountryDetailsView(context: Context, attrs: AttributeSet) : NestedScrollView(context, attrs) {

    @Inject
    lateinit var resourceProvider: ResourceProvider

    @Inject
    lateinit var toastProvider: ToastProvider

    @Inject
    lateinit var analytics: Analytics

    override fun onFinishInflate() {
        super.onFinishInflate()

        (context as InjectingDrawerActivity)
                .component
                .inject(this)
    }

    fun setCountry(country: Country) {
        country_region.text = country.subregion
        country_capital.text = country.capital
        country_description.text = country.description
    }

    private fun showToast(@StringRes resource: Int) {
        get(toastProvider).showCancelableToast(resource, Toast.LENGTH_LONG)
    }
}