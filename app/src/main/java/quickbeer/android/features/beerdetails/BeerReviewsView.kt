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

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.FrameLayout
import quickbeer.android.R
import quickbeer.android.data.pojos.Review
import quickbeer.android.listeners.LoadMoreListener

class BeerReviewsView : FrameLayout {

    private lateinit var beerReviewsAdapter: BeerReviewsAdapter

    private lateinit var beerReviewsListView: RecyclerView

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onFinishInflate() {
        super.onFinishInflate()

        beerReviewsAdapter = BeerReviewsAdapter()

        beerReviewsListView = findViewById(R.id.beers_reviews_list_view) as RecyclerView
        beerReviewsListView.setHasFixedSize(true)
        beerReviewsListView.layoutManager = LinearLayoutManager(context)
        beerReviewsListView.adapter = beerReviewsAdapter
    }

    fun setReviews(reviews: List<Review>) {
        beerReviewsAdapter.setReviews(reviews)
    }

    fun setOnScrollListener(listener: LoadMoreListener) {
        listener.setLayoutManager(beerReviewsListView.layoutManager as LinearLayoutManager)
        beerReviewsListView.addOnScrollListener(listener)
    }
}
