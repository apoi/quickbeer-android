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

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.adapters.BaseListAdapter
import quickbeer.android.data.pojos.Review
import java.util.ArrayList

class BeerReviewsAdapter : BaseListAdapter() {

    private val reviews = ArrayList<Review>(10)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.beer_details_review, parent, false)
        return BeerReviewsViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BeerReviewsViewHolder).setReview(getItem(position))
    }

    fun getItem(position: Int): Review {
        return reviews[position]
    }

    override fun getItemViewType(position: Int): Int {
        return BaseListAdapter.ItemType.REVIEW.ordinal
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    fun setReviews(reviews: List<Review>) {
        if (reviews != this.reviews) {
            this.reviews.clear()
            this.reviews.addAll(reviews)

            notifyDataSetChanged()
        }
    }
}
