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

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt
import quickbeer.android.R
import quickbeer.android.databinding.BeerDetailsInfoFragmentBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.rating.RatingBinder
import quickbeer.android.domain.style.Style
import quickbeer.android.feature.beerdetails.model.Address
import quickbeer.android.feature.beerdetails.model.OwnRating
import quickbeer.android.feature.login.LoginDialog
import quickbeer.android.navigation.Destination
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ToastProvider
import quickbeer.android.util.ktx.formatDateTime
import quickbeer.android.util.ktx.observeSuccess
import quickbeer.android.util.ktx.setNegativeAction
import quickbeer.android.util.ktx.setPositiveAction
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class BeerDetailsInfoFragment : BaseFragment(R.layout.beer_details_info_fragment) {

    @Inject
    lateinit var toastProvider: ToastProvider

    private val binding by viewBinding(BeerDetailsInfoFragmentBinding::bind)
    private val viewModel by viewModels<BeerDetailsViewModel>()

    private var scrollListener: OnFragmentScrollListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.beerRatingOverall.setOnClickListener {
            showToast(R.string.description_rating_overall)
        }

        binding.beerRatingStyle.setOnClickListener {
            showToast(R.string.description_rating_style)
        }

        binding.beerRatingAbv.setOnClickListener {
            showToast(R.string.description_abv)
        }

        binding.beerRatingIbu.setOnClickListener {
            showToast(R.string.description_ibu)
        }

        binding.detailsView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                scrollListener?.onScrollDown()
            } else if (scrollY < oldScrollY) {
                scrollListener?.onScrollUp()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        scrollListener = parentFragment as OnFragmentScrollListener
    }

    override fun onDetach() {
        super.onDetach()
        scrollListener = null
    }

    override fun observeViewState() {
        observeSuccess(viewModel.beerState, ::setBeer)
        observeSuccess(viewModel.brewerState, ::setBrewer)
        observeSuccess(viewModel.styleState, ::setStyle)
        observeSuccess(viewModel.addressState, ::setAddress)
        observeSuccess(viewModel.ratingState, ::setRating)
    }

    private fun setBeer(beer: Beer) {
        binding.infoDescription.value = beer.description
            ?.takeIf(String::isNotEmpty)
            ?: getString(R.string.no_description)

        binding.beerRatingOverall.value = beer.overallRating
            ?.takeIf { it > 0 }
            ?.roundToInt()
            ?.toString()
            ?: "?"

        binding.beerRatingStyle.value = beer.styleRating
            ?.takeIf { it > 0 }
            ?.roundToInt()
            ?.toString()
            ?: "?"

        binding.beerRatingAbv.value = beer.alcohol
            ?.takeIf { it > 0 }
            ?.toString()
            ?.let { "%s%%".format(it) }
            ?: "?"

        binding.beerRatingIbu.value = beer.ibu
            ?.takeIf { it > 0 }
            ?.roundToInt()
            ?.toString()
            ?: getString(R.string.not_available)
    }

    private fun setRating(ownRating: OwnRating) {
        if (ownRating.rating != null) {
            // Rating is shown if available
            RatingBinder.bind(requireContext(), ownRating.rating, binding.ownRating)
            binding.ownRating.ratingCard.isVisible = true
        } else if (ownRating.tick != null) {
            // Tick is shown if available and no rating
            binding.ratingBar.rating = ownRating.tick.toFloat()
            binding.tickedDate.text = ownRating.tickDate
                ?.formatDateTime(getString(R.string.beer_tick_date))
            binding.starRatingCard.isVisible = true
        }
    }

    private fun setBrewer(brewer: Brewer) {
        binding.infoBrewer.value = brewer.name
        binding.infoBrewer.setOnClickListener {
            navigate(Destination.Brewer(brewer.id))
        }
    }

    private fun setStyle(style: Style) {
        binding.infoStyle.value = style.name
        binding.infoStyle.setOnClickListener {
            navigate(Destination.Style(style.id))
        }
    }

    private fun setAddress(address: Address) {
        binding.infoOrigin.value = address.cityAndCountry()
        binding.infoOrigin.setOnClickListener {
            navigate(Destination.Country(address.countryId))
        }
    }

    private fun showToast(@StringRes resource: Int) {
        toastProvider.showCancelableToast(resource, Toast.LENGTH_LONG)
    }

    companion object {
        fun create(beerId: Int): Fragment {
            return BeerDetailsInfoFragment().apply {
                arguments = bundleOf(NavParams.ID to beerId)
            }
        }
    }
}
