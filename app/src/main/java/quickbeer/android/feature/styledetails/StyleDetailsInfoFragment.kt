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

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.StyleDetailsInfoFragmentBinding
import quickbeer.android.domain.style.Style
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

class StyleDetailsInfoFragment : BaseFragment(R.layout.style_details_info_fragment) {

    private val binding by viewBinding(StyleDetailsInfoFragmentBinding::bind)
    private val viewModel by viewModel<StyleDetailsViewModel> {
        parametersOf(requireArguments().getInt(NavParams.ID))
    }

    override fun observeViewState() {
        observe(viewModel.styleState) { state ->
            when (state) {
                is State.Loading -> Unit
                State.Empty -> Unit
                is State.Success -> setStyle(state.value)
                is State.Error -> Unit
            }
        }

        observe(viewModel.parentStyleState) { state ->
            when (state) {
                is State.Loading -> Unit
                State.Empty -> Unit
                is State.Success -> setParentStyle(state.value)
                is State.Error -> Unit
            }
        }
    }

    private fun setStyle(style: Style) {
        binding.description.value = style.description
    }

    private fun setParentStyle(style: Style) {
        binding.parent.value = style.name
    }

    companion object {
        fun create(styleId: Int): Fragment {
            return StyleDetailsInfoFragment().apply {
                arguments = bundleOf(NavParams.ID to styleId)
            }
        }
    }
}
