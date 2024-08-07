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
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.transform.BlurTransformation
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.DetailsFragmentBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.ui.base.MainFragment
import quickbeer.android.ui.transformations.ContainerLabelExtractor
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class BeerDetailsFragment : MainFragment(R.layout.details_fragment) {

    private val binding by viewBinding(DetailsFragmentBinding::bind)
    private val viewModel by viewModels<BeerDetailsViewModel>()
    private val args by navArgs<BeerDetailsFragmentArgs>()

    override fun topInsetView() = binding.toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = BeerDetailsPagerAdapter(childFragmentManager, args.id)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    override fun observeViewState() {
        observe(viewModel.beerState) { state ->
            when (state) {
                is State.Success -> setBeer(state.value)
                else -> Unit
            }
        }
    }

    private fun setBeer(beer: Beer) {
        val onImageLoadSuccess = {
            binding.collapsingToolbarBackground.setOnClickListener {
                navigate(BeerDetailsFragmentDirections.toPhoto(beer.imageUri()))
            }
        }

        binding.collapsingToolbar.title = beer.name
        binding.collapsingToolbarBackground.load(beer.imageUri()) {
            crossfade(resources.getInteger(android.R.integer.config_shortAnimTime))
            transformations(
                ContainerLabelExtractor(LABEL_WIDTH, LABEL_HEIGHT),
                BlurTransformation(requireContext(), LABEL_BLUR)
            )
            listener(object : ImageRequest.Listener {
                override fun onSuccess(request: ImageRequest, metadata: ImageResult.Metadata) {
                    onImageLoadSuccess()
                }
            })
        }
    }

    companion object {
        private const val LABEL_WIDTH = 300
        private const val LABEL_HEIGHT = 300
        private const val LABEL_BLUR = 15f
    }
}
