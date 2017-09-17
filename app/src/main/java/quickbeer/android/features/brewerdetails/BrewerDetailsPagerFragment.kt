/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela></antti.poikela>@iki.fi>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.features.brewerdetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.details_fragment_pager.*
import polanski.option.AtomicOption
import polanski.option.Option.ofObj
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.analytics.Events.Screen
import quickbeer.android.core.fragment.BaseFragment
import timber.log.Timber
import javax.inject.Inject

class BrewerDetailsPagerFragment : BaseFragment() {

    @Inject
    internal lateinit var analytics: Analytics

    private var brewerId: Int = 0

    companion object {
        fun newInstance(brewerId: Int): Fragment {
            val fragment = BrewerDetailsPagerFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.ID_KEY, brewerId)
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ofObj(savedInstanceState ?: arguments)
                .map { it.getInt(Constants.ID_KEY) }
                .ifSome { brewerId = it }
                .ifNone { Timber.w("Expected state for initializing!") }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.details_fragment_pager, container, false)
    }

    override fun inject() {
        component.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view_pager.adapter = BrewerDetailsPagerAdapter(childFragmentManager, brewerId)
        tab_layout.setupWithViewPager(view_pager)

        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val screen = if (position == 0)
                    Screen.BREWER_DETAILS
                else
                    Screen.BREWER_BEERS
                analytics.createEvent(screen)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(Constants.ID_KEY, brewerId)
        super.onSaveInstanceState(outState)
    }
}
