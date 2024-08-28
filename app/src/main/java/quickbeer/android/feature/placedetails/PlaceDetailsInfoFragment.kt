/**
 * This file is part of QuickBeer.
 * Copyright (C) 2024 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.feature.placedetails

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.databinding.PlaceDetailsInfoFragmentBinding
import quickbeer.android.domain.place.Place
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.observeSuccess
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class PlaceDetailsInfoFragment : BaseFragment(R.layout.place_details_info_fragment) {

    private val binding by viewBinding(PlaceDetailsInfoFragmentBinding::bind)
    private val viewModel by viewModels<PlaceDetailsViewModel>()

    override fun observeViewState() {
        observeSuccess(viewModel.placeState, ::setPlace)
    }

    private fun setPlace(country: Place) {

    }

    companion object {
        fun create(placeId: Int): Fragment {
            return PlaceDetailsInfoFragment().apply {
                arguments = bundleOf(NavParams.ID to placeId)
            }
        }
    }
}
