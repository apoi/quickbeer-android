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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.details_fragment_pager.*
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.analytics.Events.Screen
import quickbeer.android.core.fragment.BaseFragment
import timber.log.Timber
import javax.inject.Inject

class StyleDetailsPagerFragment : BaseFragment() {

    @Inject
    lateinit var analytics: Analytics

    private var styleId: Int = 0

    private var defaultIndex: Int = 0

    companion object {
        fun newInstance(styleId: Int, defaultIndex: Int = 0): Fragment {
            val bundle = Bundle()
            bundle.putInt(Constants.ID_KEY, styleId)
            bundle.putInt(Constants.PAGER_INDEX, defaultIndex)
            val fragment = StyleDetailsPagerFragment()
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
        styleId = bundle?.getInt(Constants.ID_KEY) ?: 0
        defaultIndex = bundle?.getInt(Constants.PAGER_INDEX) ?: 0

        if (styleId == 0) {
            Timber.w("Expected state for initializing!")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.details_fragment_pager, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view_pager.apply {
            adapter = StyleDetailsPagerAdapter(childFragmentManager, styleId)
            setCurrentItem(defaultIndex)
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    analytics.createEvent(
                        if (position == 0) Screen.STYLE_DETAILS
                        else Screen.STYLE_BEERS
                    )
                }
            })
        }

        tab_layout.setupWithViewPager(view_pager)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(Constants.ID_KEY, styleId)
        super.onSaveInstanceState(outState)
    }
}
