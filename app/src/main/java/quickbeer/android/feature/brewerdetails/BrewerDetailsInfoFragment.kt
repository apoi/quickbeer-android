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
package quickbeer.android.feature.brewerdetails

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.databinding.BrewerDetailsInfoFragmentBinding
import quickbeer.android.databinding.DetailsInfoColumnsBinding
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.feature.beerdetails.model.Address
import quickbeer.android.navigation.Destination
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.binder.InfoColumnsBinder
import quickbeer.android.util.ktx.observeSuccess
import quickbeer.android.util.ktx.openMaps
import quickbeer.android.util.ktx.openWikipedia
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class BrewerDetailsInfoFragment : BaseFragment(R.layout.brewer_details_info_fragment) {

    private val binding by viewBinding(BrewerDetailsInfoFragmentBinding::bind)
    private val columnBinding by viewBinding(DetailsInfoColumnsBinding::bind)

    private val viewModel by viewModels<BrewerDetailsViewModel>()

    override fun observeViewState() {
        observeSuccess(viewModel.brewerState, ::setBrewer)
        observeSuccess(viewModel.addressState, ::setAddress)
    }

    private fun setBrewer(brewer: Brewer) {
        InfoColumnsBinder.bind(requireContext(), brewer, columnBinding)
    }

    private fun setAddress(address: Address) {
        val notAvailable = getString(R.string.not_available)

        binding.city.value = address.city?.also {
            binding.city.setOnClickListener { requireContext().openWikipedia(address.city) }
        } ?: notAvailable

        binding.address.value = address.address?.also {
            binding.address.setOnClickListener { requireContext().openMaps(address) }
        } ?: notAvailable

        binding.country.value = address.country
        binding.country.setOnClickListener {
            navigate(Destination.Country(address.countryId))
        }
    }

    companion object {
        fun create(brewerId: Int): Fragment {
            return BrewerDetailsInfoFragment().apply {
                arguments = bundleOf(NavParams.ID to brewerId)
            }
        }
    }
}
