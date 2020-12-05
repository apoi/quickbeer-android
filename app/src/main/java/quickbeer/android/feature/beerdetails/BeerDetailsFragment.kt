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
package quickbeer.android.feature.beerdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import org.koin.ext.getOrCreateScope
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.databinding.BeerDetailsFragmentBinding
import quickbeer.android.databinding.BeerDetailsFragmentPagerBinding
import quickbeer.android.feature.beerdetails.BeerDetailsPagerAdapter
import quickbeer.android.feature.search.SearchFragmentArgs
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.viewBinding
import timber.log.Timber

class BeerDetailsFragment : BaseFragment(R.layout.beer_details_fragment_pager) {

    private val binding by viewBinding(BeerDetailsFragmentPagerBinding::bind)
    private val args: BeerDetailsFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewPager.adapter = BeerDetailsPagerAdapter(childFragmentManager, args.id)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }
}
