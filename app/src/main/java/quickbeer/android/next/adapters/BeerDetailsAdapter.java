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
package quickbeer.android.next.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import quickbeer.android.next.R;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.Review;
import quickbeer.android.next.views.viewholders.BeerDetailsViewHolder;
import quickbeer.android.next.views.viewholders.ReviewViewHolder;

public class BeerDetailsAdapter extends BaseListAdapter {

    private Beer beer;
    private List<Review> reviews;

    public BeerDetailsAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.BEER.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_details_view, parent, false);
            return new BeerDetailsViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_details_review, parent, false);
            return new ReviewViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemType type = ItemType.values()[(getItemViewType(position))];

        switch (type) {
            case BEER:
                ((BeerDetailsViewHolder) holder).setBeer(beer);
                break;
            case REVIEW:
                ((ReviewViewHolder) holder).setReview(getItem(position));
                break;
        }
    }

    public Review getItem(int position) {
        return reviews.get(position - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ItemType.BEER.ordinal();
        } else {
            return ItemType.REVIEW.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        return (beer != null ? 1 : 0) +
                (reviews != null ? reviews.size() : 0);
    }

    public void setBeer(Beer beer) {
        if (!beer.equals(this.beer)) {
            this.beer = beer;

            notifyDataSetChanged();
        }
    }

    public void setReviews(List<Review> reviews) {
        if (!reviews.equals(this.reviews)) {
            this.reviews = reviews;

            notifyDataSetChanged();
        }
    }
}
