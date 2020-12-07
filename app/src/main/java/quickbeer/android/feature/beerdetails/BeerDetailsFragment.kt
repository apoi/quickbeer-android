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
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.BeerDetailsFragmentBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.transformations.BlurTransformation
import quickbeer.android.ui.transformations.ContainerLabelExtractor
import quickbeer.android.util.ktx.setMarginTop
import quickbeer.android.util.ktx.viewBinding

class BeerDetailsFragment : BaseFragment(R.layout.beer_details_fragment) {

    private val args: BeerDetailsFragmentArgs by navArgs()
    private val binding by viewBinding(BeerDetailsFragmentBinding::bind)
    private val viewModel by viewModel<BeerDetailsViewModel> { parametersOf(args.id) }

    private val picasso by inject<Picasso>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.layout) { _, insets ->
            binding.toolbar.setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }

        binding.viewPager.adapter = BeerDetailsPagerAdapter(childFragmentManager, args.id)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    override fun observeViewState() {
        observe(viewModel.viewState) { state ->
            when (state) {
                State.Loading -> Unit
                State.Empty -> Unit
                is State.Success -> setBeer(state.value)
                is State.Error -> Unit
            }
        }
    }

    private fun setBeer(beer: Beer) {
        val onImageLoadSuccess = {
            binding.toolbarOverlayGradient.visibility = View.VISIBLE
            binding.collapsingToolbarBackground.setOnClickListener {
                // openPhotoView(beer.imageUri())
            }
        }

        binding.collapsingToolbar.title = beer.name

        picasso.load(beer.imageUri())
            .transform(ContainerLabelExtractor(LABEL_WIDTH, LABEL_HEIGHT))
            .transform(BlurTransformation(requireContext(), LABEL_BLUR))
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
        private const val LABEL_WIDTH = 300
        private const val LABEL_HEIGHT = 300
        private const val LABEL_BLUR = 15
    }
}
