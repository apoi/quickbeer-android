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
package quickbeer.android.features.styledetails

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import io.reark.reark.utils.Preconditions.get
import polanski.option.Option.ofObj
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.analytics.Events.Screen
import quickbeer.android.core.fragment.BaseFragment
import quickbeer.android.features.brewerdetails.BrewerDetailsPagerAdapter
import timber.log.Timber
import javax.inject.Inject

class StyleDetailsPagerFragment : BaseFragment() {

    @BindView(R.id.view_pager)
    lateinit var viewPager: ViewPager

    @BindView(R.id.tab_layout)
    lateinit var tabLayout: TabLayout

    @Inject
    lateinit var analytics: Analytics

    private var styleId: Int = 0

    companion object {
        fun newInstance(brewerId: Int): Fragment {
            val bundle = Bundle()
            bundle.putInt("styleId", brewerId)
            val fragment = StyleDetailsPagerFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun inject() {
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = savedInstanceState ?: arguments

        ofObj(bundle)
                .map { state -> state.getInt("styleId") }
                .ifSome { value -> styleId = value!! }
                .ifNone { Timber.w("Expected state for initializing!") }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.details_fragment_pager, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewPager.apply {
            adapter = BrewerDetailsPagerAdapter(childFragmentManager, styleId)
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    val screen = if (position == 0)
                        Screen.BREWER_DETAILS
                    else
                        Screen.BREWER_BEERS
                    get(analytics).createEvent(screen)
                }
            })
        }

        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("styleId", styleId)
        super.onSaveInstanceState(outState)
    }

}
