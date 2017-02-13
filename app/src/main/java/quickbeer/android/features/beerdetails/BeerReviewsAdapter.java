/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.features.beerdetails;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import quickbeer.android.R;
import quickbeer.android.adapters.BaseListAdapter;
import quickbeer.android.data.pojos.Review;

public class BeerReviewsAdapter extends BaseListAdapter {

    @NonNull
    private final List<Review> reviews = new ArrayList<>(10);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_details_review, parent, false);
        return new BeerReviewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BeerReviewsViewHolder) holder).setReview(getItem(position));
    }

    public Review getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return ItemType.REVIEW.ordinal();
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(@NonNull List<Review> reviews) {
        if (!reviews.equals(this.reviews)) {
            this.reviews.clear();
            this.reviews.addAll(reviews);

            notifyDataSetChanged();
        }
    }
}
