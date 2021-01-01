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
import android.widget.RatingBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.roundToInt
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import quickbeer.android.R
import quickbeer.android.databinding.BeerDetailsInfoFragmentBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.style.Style
import quickbeer.android.feature.beerdetails.model.Address
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ToastProvider
import quickbeer.android.util.ktx.formatDateTime
import quickbeer.android.util.ktx.observeSuccess
import quickbeer.android.util.ktx.viewBinding

class BeerDetailsInfoFragment :
    BaseFragment(R.layout.beer_details_info_fragment),
    RatingBar.OnRatingBarChangeListener,
    SwipeRefreshLayout.OnRefreshListener {

    private val binding by viewBinding(BeerDetailsInfoFragmentBinding::bind)
    private val viewModel by viewModel<BeerDetailsViewModel> {
        parametersOf(requireArguments().getInt(NavParams.ID))
    }

    private val toastProvider by inject<ToastProvider>()

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

        // binding.ratingCardOverlay.setOnClickListener { showLoginDialog() }
    }

    override fun observeViewState() {
        observeSuccess(viewModel.beerState, ::setBeer)
        observeSuccess(viewModel.brewerState, ::setBrewer)
        observeSuccess(viewModel.styleState, ::setStyle)
        observeSuccess(viewModel.addressState, ::setAddress)
    }

    private fun setBeer(beer: Beer) {
        binding.description.value = beer.description
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

        binding.ratingBar.rating = beer.tickValue
            ?.takeIf { beer.isTicked() }
            ?.toFloat()
            ?: 0F

        binding.tickedDate.text = beer.tickDate
            ?.takeIf { beer.isTicked() }
            ?.formatDateTime(getString(R.string.beer_tick_date))
    }

    private fun setBrewer(brewer: Brewer) {
        binding.brewer.value = brewer.name
        binding.brewer.setOnClickListener {
            navigate(BeerDetailsFragmentDirections.toBrewer(brewer.id))
        }
    }

    private fun setStyle(style: Style) {
        binding.style.value = style.name
        binding.style.setOnClickListener {
            navigate(BeerDetailsFragmentDirections.toStyle(style.id))
        }
    }

    private fun setAddress(address: Address) {
        binding.origin.value = address.cityAndCountry()
        binding.origin.setOnClickListener {
            navigate(BeerDetailsFragmentDirections.toCountry(address.countryId))
        }
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
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
