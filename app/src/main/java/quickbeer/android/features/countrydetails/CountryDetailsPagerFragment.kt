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

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reark.reark.utils.Preconditions.get
import kotlinx.android.synthetic.main.details_fragment_pager.*
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.analytics.Events.Screen
import quickbeer.android.core.fragment.BaseFragment
import timber.log.Timber
import javax.inject.Inject

class CountryDetailsPagerFragment : BaseFragment() {

    @Inject
    lateinit var analytics: Analytics

    private var countryId: Int = 0

    private var defaultIndex: Int = 0

    companion object {
        fun newInstance(countryId: Int, defaultIndex: Int = 0): Fragment {
            val bundle = Bundle()
            bundle.putInt(Constants.ID_KEY, countryId)
            bundle.putInt(Constants.PAGER_INDEX, defaultIndex)
            val fragment = CountryDetailsPagerFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun inject() {
        getComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = savedInstanceState ?: arguments
        countryId = bundle?.getInt(Constants.ID_KEY) ?: 0
        defaultIndex = bundle.getInt(Constants.PAGER_INDEX)

        if (countryId == 0) {
            Timber.w("Expected state for initializing!")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.details_fragment_pager, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view_pager.apply {
            adapter = CountryDetailsPagerAdapter(childFragmentManager, countryId)
            setCurrentItem(defaultIndex)
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    get(analytics).createEvent(
                            if (position == 0) Screen.COUNTRY_DETAILS
                            else Screen.COUNTRY_BEERS
                    )
                }
            })
        }

        tab_layout.setupWithViewPager(view_pager)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(Constants.ID_KEY, countryId)
        super.onSaveInstanceState(outState)
    }
}
