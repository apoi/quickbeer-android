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

import android.view.View
import androidx.annotation.StringRes
import coil.load
import coil.transform.CircleCropTransformation
import java.lang.String.valueOf
import quickbeer.android.R
import quickbeer.android.databinding.RatingListItemBinding
import quickbeer.android.domain.rating.Rating
import quickbeer.android.ui.adapter.base.ListViewHolder
import quickbeer.android.ui.adapter.rating.RatingListModel
import quickbeer.android.util.ktx.formatDate

/**
 * View holder for ratings in list
 */
class BeerRatingsViewHolder(
    private val binding: RatingListItemBinding
) : ListViewHolder<RatingListModel>(binding.root) {

    override fun bind(item: RatingListModel) {
        val metadata = item.rating.userName +
            (item.rating.country?.let { ", $it" } ?: "") +
            "\n" + item.rating.timeEntered?.formatDate()

        binding.user.text = metadata
        binding.score.text = String.format("%.1f", item.rating.totalScore)
        binding.description.text = item.rating.comments

        binding.avatar.load(item.rating.avatarUri()) {
            crossfade(itemView.context.resources.getInteger(android.R.integer.config_shortAnimTime))
            transformations(CircleCropTransformation())
        }

        if (item.rating.appearance != null) {
            setDetails(item.rating)
            binding.detailsRow.visibility = View.VISIBLE
        } else {
            binding.detailsRow.visibility = View.GONE
        }
    }

    private fun setDetails(rating: Rating) {
        binding.appearance.text = valueOf(rating.appearance)
        binding.aroma.text = valueOf(rating.aroma)
        binding.flavor.text = valueOf(rating.flavor)
        binding.mouthfeel.text = valueOf(rating.mouthfeel)
        binding.overall.text = valueOf(rating.overall)

        binding.appearanceColumn.setOnClickListener { showToast(R.string.rating_appearance_hint) }
        binding.aromaColumn.setOnClickListener { showToast(R.string.rating_aroma_hint) }
        binding.flavorColumn.setOnClickListener { showToast(R.string.rating_flavor_hint) }
        binding.mouthfeelColumn.setOnClickListener { showToast(R.string.rating_mouthfeel_hint) }
        binding.overallColumn.setOnClickListener { showToast(R.string.rating_overall_hint) }
    }

    private fun showToast(@StringRes resource: Int) {
        // toastProvider.showCancelableToast(resource, Toast.LENGTH_LONG)
    }
}
