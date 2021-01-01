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

import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.BrewerDetailsInfoFragmentBinding
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.feature.beerdetails.model.Address
import quickbeer.android.navigation.Destination
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.ifNull
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

class BrewerDetailsInfoFragment : BaseFragment(R.layout.brewer_details_info_fragment) {

    private val binding by viewBinding(BrewerDetailsInfoFragmentBinding::bind)
    private val viewModel by viewModel<BrewerDetailsViewModel> {
        parametersOf(requireArguments().getInt(NavParams.ID))
    }

    override fun observeViewState() {
        observe(viewModel.brewerState) { state ->
            when (state) {
                is State.Loading -> Unit
                State.Empty -> Unit
                is State.Success -> setBrewer(state.value)
                is State.Error -> Unit
            }
        }

        observe(viewModel.addressState) { state ->
            when (state) {
                is State.Loading -> Unit
                State.Empty -> Unit
                is State.Success -> setAddress(state.value)
                is State.Error -> Unit
            }
        }
    }

    private fun setBrewer(brewer: Brewer) {
        val notAvailable = getString(R.string.not_available)

        binding.founded.value = brewer.founded?.year?.toString() ?: notAvailable

        brewer.website
            ?.takeIf(String::isNotEmpty)
            ?.let(::fixUrl)
            ?.let { url ->
                binding.webContainer.alpha = VISIBLE
                binding.webContainer.setOnClickListener { openUri(url) }
            }.ifNull {
                binding.web.alpha = OPAQUE
            }

        brewer.facebook
            ?.takeIf(String::isNotEmpty)
            ?.let { Constants.FACEBOOK_PATH.format(it) }
            ?.let { url ->
                binding.facebookContainer.alpha = VISIBLE
                binding.facebookContainer.setOnClickListener { openUri(url) }
            }.ifNull {
                binding.facebook.alpha = OPAQUE
            }

        brewer.twitter
            ?.takeIf(String::isNotEmpty)
            ?.let { Constants.TWITTER_PATH.format(it) }
            ?.let { url ->
                binding.twitterContainer.alpha = VISIBLE
                binding.twitterContainer.setOnClickListener { openUri(url) }
            }.ifNull {
                binding.twitter.alpha = OPAQUE
            }
    }

    private fun setAddress(address: Address) {
        val notAvailable = getString(R.string.not_available)

        binding.city.value = address.city
            ?.also { binding.city.setOnClickListener { openWikipedia(address.city) } }
            ?: notAvailable

        binding.address.value = address.address
            ?.also { binding.address.setOnClickListener { openMaps(address) } }
            ?: notAvailable

        binding.country.value = address.country
        binding.country.setOnClickListener {
            navigate(Destination.Country(address.countryId))
        }
    }

    private fun fixUrl(url: String?): String? {
        return addMissingProtocol(removeTrailingSlash(url))
    }

    private fun addMissingProtocol(value: String?): String? {
        return when {
            value == null -> null
            !value.startsWith("http") -> "http://$value"
            else -> value
        }
    }

    private fun removeTrailingSlash(value: String?): String? {
        return when {
            value == null -> null
            value.endsWith("/") -> value.dropLast(1)
            else -> value
        }
    }

    private fun openUri(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        requireContext().startActivity(intent)
    }

    private fun openWikipedia(article: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WIKIPEDIA_PATH.format(article)))
        requireContext().startActivity(intent)
    }

    private fun openMaps(address: Address) {
        if (address.address == null) return

        val street = if (address.address.contains(",")) {
            address.address.split(",")[0]
        } else address.address

        val link = "%s, %s, %s".format(street, address.city, address.country)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GOOGLE_MAPS_PATH.format(link)))
        requireContext().startActivity(intent)
    }

    companion object {
        private const val VISIBLE = 1.0f
        private const val OPAQUE = 0.4f

        fun create(brewerId: Int): Fragment {
            return BrewerDetailsInfoFragment().apply {
                arguments = bundleOf(NavParams.ID to brewerId)
            }
        }
    }
}
