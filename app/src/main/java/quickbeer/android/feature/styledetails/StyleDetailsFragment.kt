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
package quickbeer.android.feature.styledetails

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.DetailsFragmentBinding
import quickbeer.android.domain.style.Style
import quickbeer.android.ui.base.MainFragment
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class StyleDetailsFragment : MainFragment(R.layout.details_fragment) {

    private val binding by viewBinding(DetailsFragmentBinding::bind)
    private val viewModel by viewModels<StyleDetailsViewModel>()
    private val args: StyleDetailsFragmentArgs by navArgs()

    override fun topInsetView() = binding.toolbar

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewPager.adapter = StyleDetailsPagerAdapter(childFragmentManager, args.id)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    override fun observeViewState() {
        observe(viewModel.styleState) { state ->
            when (state) {
                is State.Initial -> Unit
                is State.Loading -> Unit
                is State.Empty -> Unit
                is State.Success -> setStyle(state.value)
                is State.Error -> Unit
            }
        }
    }

    private fun setStyle(style: Style) {
        binding.collapsingToolbar.title = style.name
    }
}
