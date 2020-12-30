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

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.DetailsFragmentBinding
import quickbeer.android.domain.country.Country
import quickbeer.android.ui.base.MainFragment
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

class CountryDetailsFragment : MainFragment(R.layout.details_fragment) {

    override fun rootLayout() = binding.layout
    override fun topInsetView() = binding.toolbar

    private val args: CountryDetailsFragmentArgs by navArgs()
    private val binding by viewBinding(DetailsFragmentBinding::bind)
    private val viewModel by viewModel<CountryDetailsViewModel> { parametersOf(args.id) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewPager.adapter = CountryDetailsPagerAdapter(childFragmentManager, args.id)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    override fun observeViewState() {
        observe(viewModel.countryState) { state ->
            when (state) {
                State.Loading -> Unit
                State.Empty -> Unit
                is State.Success -> setCountry(state.value)
                is State.Error -> Unit
            }
        }
    }

    private fun setCountry(country: Country) {
        binding.collapsingToolbar.title = country.name
    }
}
