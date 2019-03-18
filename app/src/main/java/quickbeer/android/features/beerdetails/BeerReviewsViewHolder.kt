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
package quickbeer.android.features.beerdetails

import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.reark.reark.utils.Preconditions.checkNotNull
import kotlinx.android.synthetic.main.beer_details_review.view.*
import quickbeer.android.R
import quickbeer.android.core.activity.InjectingDrawerActivity
import quickbeer.android.data.pojos.Review
import quickbeer.android.providers.ToastProvider
import quickbeer.android.utils.kotlin.formatDate
import java.lang.String.valueOf
import javax.inject.Inject

/**
 * View holder for reviews in list
 */
class BeerReviewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val score: TextView = view.review_score
    val description: TextView = view.review_description
    val user: TextView = view.review_user
    val detailsRow: View = view.review_details_row
    val appearance: TextView = view.review_appearance
    val appearanceColumn: View = view.review_appearance_column
    val aroma: TextView = view.review_aroma
    val aromaColumn: View = view.review_aroma_column
    val flavor: TextView = view.review_flavor
    val flavorColumn: View = view.review_flavor_column
    val mouthfeel: TextView = view.review_mouthfeel
    val mouthfeelColumn: View = view.review_mouthfeel_column
    val overall: TextView = view.review_overall
    val overallColumn: View = view.review_overall_column

    @Inject
    internal lateinit var toastProvider: ToastProvider

    init {
        (view.context as InjectingDrawerActivity)
            .getComponent()
            .inject(this)
    }

    fun setReview(review: Review) {
        checkNotNull(review)

        val metadata = (review.userName +
            (if (review.country != null) ", " + review.country else "") +
            "\n" + review.timeEntered.formatDate())

        user.text = metadata
        score.text = String.format("%.1f", review.totalScore)
        description.text = review.comments

        if (review.appearance != null) {
            setDetails(review)
            detailsRow.visibility = View.VISIBLE
        } else {
            detailsRow.visibility = View.GONE
        }
    }

    private fun setDetails(review: Review) {
        appearance.text = valueOf(review.appearance)
        aroma.text = valueOf(review.aroma)
        flavor.text = valueOf(review.flavor)
        mouthfeel.text = valueOf(review.mouthfeel)
        overall.text = valueOf(review.overall)

        appearanceColumn.setOnClickListener { showToast(R.string.review_appearance_hint) }
        aromaColumn.setOnClickListener { showToast(R.string.review_aroma_hint) }
        flavorColumn.setOnClickListener { showToast(R.string.review_flavor_hint) }
        mouthfeelColumn.setOnClickListener { showToast(R.string.review_mouthfeel_hint) }
        overallColumn.setOnClickListener { showToast(R.string.review_overall_hint) }
    }

    private fun showToast(@StringRes resource: Int) {
        toastProvider.showCancelableToast(resource, Toast.LENGTH_LONG)
    }
}