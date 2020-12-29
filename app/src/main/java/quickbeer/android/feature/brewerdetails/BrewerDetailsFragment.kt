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

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.DetailsFragmentBinding
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.ui.base.MainFragment
import quickbeer.android.ui.transformations.BlurTransformation
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

class BrewerDetailsFragment : MainFragment(R.layout.details_fragment) {

    override fun rootLayout() = binding.layout
    override fun topInsetView() = binding.toolbar

    private val args: BrewerDetailsFragmentArgs by navArgs()
    private val binding by viewBinding(DetailsFragmentBinding::bind)
    private val viewModel by viewModel<BrewerDetailsViewModel> { parametersOf(args.id) }

    private val picasso by inject<Picasso>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewPager.adapter = BrewerDetailsPagerAdapter(childFragmentManager, args.id)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    override fun observeViewState() {
        observe(viewModel.brewerState) { state ->
            when (state) {
                State.Loading -> Unit
                State.Empty -> Unit
                is State.Success -> setBrewer(state.value)
                is State.Error -> Unit
            }
        }
    }

    private fun setBrewer(brewer: Brewer) {
        val onImageLoadSuccess = {
            binding.toolbarOverlayGradient.visibility = View.VISIBLE
            binding.collapsingToolbarBackground.setOnClickListener {
                // openPhotoView(beer.imageUri())
            }
        }

        binding.collapsingToolbar.title = brewer.name

        picasso.load(brewer.imageUri())
            .transform(BlurTransformation(requireContext(), IMAGE_BLUR))
            .into(
                binding.collapsingToolbarBackground,
                object : Callback.EmptyCallback() {
                    override fun onSuccess() {
                        onImageLoadSuccess()
                    }
                }
            )
    }

    companion object {
        private const val IMAGE_BLUR = 15
    }
}
