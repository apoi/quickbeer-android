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
package quickbeer.android.feature.countrydetails

import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.databinding.CountryDetailsInfoFragmentBinding
import quickbeer.android.domain.country.Country
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.ifNull
import quickbeer.android.util.ktx.observeSuccess
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class CountryDetailsInfoFragment : BaseFragment(R.layout.country_details_info_fragment) {

    private val binding by viewBinding(CountryDetailsInfoFragmentBinding::bind)
    private val viewModel by viewModels<CountryDetailsViewModel>()

    override fun observeViewState() {
        observeSuccess(viewModel.countryState, ::setCountry)
    }

    private fun setCountry(country: Country) {
        binding.officialName.apply {
            value = country.official
            setOnClickListener { openWikipedia(country.wikipedia) }
        }

        binding.region.apply {
            value = country.subregion
            setOnClickListener { openWikipedia(country.subregion) }
        }

        binding.capital.apply {
            value = country.capital
            setOnClickListener { openWikipedia(country.capital) }
        }

        country.description
            ?.let { binding.beerCulture.value = it }
            .ifNull { binding.beerCultureGroup.isVisible = false }

        country.wikipediaBeer?.let { link ->
            binding.beerCultureLink.setOnClickListener { openWikipedia(link) }
        }
    }

    private fun openWikipedia(article: String) {
        val uri = Uri.parse(Constants.WIKIPEDIA_PATH.format(article))
        requireContext().startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    companion object {
        fun create(countryId: Int): Fragment {
            return CountryDetailsInfoFragment().apply {
                arguments = bundleOf(NavParams.ID to countryId)
            }
        }
    }
}
