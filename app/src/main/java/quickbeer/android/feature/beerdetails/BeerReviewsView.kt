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
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import quickbeer.android.R
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.listener.LoadMoreListener

class BeerReviewsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val beerReviewsAdapter = ListAdapter<ReviewModel>(ReviewTypeFactory())

    private lateinit var beerReviewsListView: RecyclerView

    override fun onFinishInflate() {
        super.onFinishInflate()

        beerReviewsListView = findViewById(R.id.beers_reviews_list_view)
        beerReviewsListView.setHasFixedSize(true)
        beerReviewsListView.layoutManager = LinearLayoutManager(context)
        beerReviewsListView.adapter = beerReviewsAdapter
    }

    fun setReviews(reviews: List<ReviewModel>) {
        beerReviewsAdapter.setItems(reviews)
    }

    fun setOnScrollListener(listener: LoadMoreListener) {
        listener.setLayoutManager(beerReviewsListView.layoutManager as LinearLayoutManager)
        beerReviewsListView.addOnScrollListener(listener)
    }
}
