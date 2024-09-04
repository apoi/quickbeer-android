/**
 * This file is part of QuickBeer.
 * Copyright (C) 2024 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.feature.placedetails

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import quickbeer.android.R
import quickbeer.android.databinding.DetailsInfoColumnsBinding
import quickbeer.android.databinding.PlaceDetailsInfoFragmentBinding
import quickbeer.android.domain.address.Address
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.place.Place
import quickbeer.android.feature.placedetails.model.PlaceDetailsState
import quickbeer.android.navigation.Destination
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.binder.InfoColumnsBinder
import quickbeer.android.util.ToastProvider
import quickbeer.android.util.ktx.ifNull
import quickbeer.android.util.ktx.observeSuccess
import quickbeer.android.util.ktx.openMaps
import quickbeer.android.util.ktx.openWikipedia
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class PlaceDetailsInfoFragment : BaseFragment(R.layout.place_details_info_fragment) {

    private val binding by viewBinding(PlaceDetailsInfoFragmentBinding::bind)
    private val columnBinding by viewBinding(DetailsInfoColumnsBinding::bind)

    private val viewModel by viewModels<PlaceDetailsInfoViewModel>()

    @Inject
    lateinit var toastProvider: ToastProvider

    override fun observeViewState() {
        observeSuccess(viewModel.viewState, ::setViewState)
    }

    private fun setViewState(viewState: PlaceDetailsState) {
        setPlace(viewState.place)
        viewState.brewer?.let(::setBrewer)
        viewState.address?.let(::setAddress)
    }

    private fun setBrewer(brewer: Brewer) {
        binding.brewer.value = brewer.name
        binding.brewer.isVisible = true
        binding.brewer.setOnClickListener { onBrewerClicked(brewer) }
    }

    private fun setPlace(place: Place) {
        val notAvailable = getString(R.string.not_available)

        InfoColumnsBinder.bind(requireContext(), place, columnBinding, ::onRatingClicked)

        place.type
            ?.let { requireContext().getString(it.stringRes) }
            ?.let { value -> binding.type.value = value }
            .ifNull { binding.type.value = notAvailable }

        place.hours
            ?.takeIf(String::isNotEmpty)
            ?.let { it.split(", ").joinToString("\n") }
            ?.let { value -> binding.openingHours.value = value }
            .ifNull { binding.openingHours.value = notAvailable }
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

    private fun onRatingClicked() {
        toastProvider.showCancelableToast(R.string.place_rating, Toast.LENGTH_LONG)
    }

    private fun onBrewerClicked(brewer: Brewer) {
        navigate(Destination.Brewer(brewer.id))
    }

    companion object {
        fun create(placeId: Int): Fragment {
            return PlaceDetailsInfoFragment().apply {
                arguments = bundleOf(NavParams.ID to placeId)
            }
        }
    }
}
