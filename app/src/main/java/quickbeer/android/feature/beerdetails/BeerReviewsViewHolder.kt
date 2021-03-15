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
import java.lang.String.valueOf
import quickbeer.android.R
import quickbeer.android.databinding.ReviewListItemBinding
import quickbeer.android.domain.review.Review
import quickbeer.android.ui.adapter.base.ListViewHolder
import quickbeer.android.ui.adapter.review.ReviewListModel

/**
 * View holder for reviews in list
 */
class BeerReviewsViewHolder(
    private val binding: ReviewListItemBinding
) : ListViewHolder<ReviewListModel>(binding.root) {

    override fun bind(item: ReviewListModel) {
        val metadata = item.review.userName +
            (item.review.country?.let { ", $it" } ?: "") +
            "\n" + item.review.timeEntered

        binding.user.text = metadata
        binding.score.text = String.format("%.1f", item.review.totalScore)
        binding.description.text = item.review.comments

        if (item.review.appearance != null) {
            setDetails(item.review)
            binding.detailsRow.visibility = View.VISIBLE
        } else {
            binding.detailsRow.visibility = View.GONE
        }
    }

    private fun setDetails(review: Review) {
        binding.appearance.text = valueOf(review.appearance)
        binding.aroma.text = valueOf(review.aroma)
        binding.flavor.text = valueOf(review.flavor)
        binding.mouthfeel.text = valueOf(review.mouthfeel)
        binding.overall.text = valueOf(review.overall)

        binding.appearanceColumn.setOnClickListener { showToast(R.string.review_appearance_hint) }
        binding.aromaColumn.setOnClickListener { showToast(R.string.review_aroma_hint) }
        binding.flavorColumn.setOnClickListener { showToast(R.string.review_flavor_hint) }
        binding.mouthfeelColumn.setOnClickListener { showToast(R.string.review_mouthfeel_hint) }
        binding.overallColumn.setOnClickListener { showToast(R.string.review_overall_hint) }
    }

    private fun showToast(@StringRes resource: Int) {
        // toastProvider.showCancelableToast(resource, Toast.LENGTH_LONG)
    }
}
