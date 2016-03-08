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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.R;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.Review;
import quickbeer.android.next.utils.ContainerLabelExtractor;
import quickbeer.android.next.utils.StringUtils;
import quickbeer.android.next.views.listitems.ItemType;

public class BeerDetailsAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

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

    /**
     * View holder for all the beer details
     */
    protected static class BeerDetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView ratingTextView;
        private TextView nameTextView;
        private TextView styleTextView;
        private TextView abvTextView;
        private TextView brewerTextView;
        private TextView locationTextView;
        private TextView descriptionTextView;
        private ImageView imageView;

        public BeerDetailsViewHolder(View view) {
            super(view);

            this.ratingTextView = (TextView) view.findViewById(R.id.beer_stars);
            this.nameTextView = (TextView) view.findViewById(R.id.beer_name);
            this.styleTextView = (TextView) view.findViewById(R.id.beer_style);
            this.abvTextView = (TextView) view.findViewById(R.id.beer_abv);
            this.brewerTextView = (TextView) view.findViewById(R.id.brewer_name);
            this.locationTextView = (TextView) view.findViewById(R.id.brewer_location);
            this.descriptionTextView = (TextView) view.findViewById(R.id.beer_description);
            this.imageView = (ImageView) view.findViewById(R.id.beer_details_image);
        }

        public void setBeer(@NonNull Beer beer) {
            Preconditions.checkNotNull(beer, "Beer cannot be null.");

            ratingTextView.setText(String.valueOf(beer.getRating()));
            nameTextView.setText(beer.getName());
            styleTextView.setText(beer.getStyleName());
            abvTextView.setText(String.format("ABV: %.1f%%", beer.getAbv()));
            brewerTextView.setText(beer.getBrewerName());
            locationTextView.setText("TODO data from brewer");
            descriptionTextView.setText(StringUtils.value(beer.getDescription(), "No description available."));

            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    final int width = imageView.getMeasuredWidth();
                    final int height = imageView.getMeasuredHeight();

                    Picasso.with(imageView.getContext())
                            .load(beer.getImageUri())
                            .transform(new ContainerLabelExtractor(width, height))
                            .into(imageView);

                    return true;
                }
            });
        }
    }

    /**
     * View holder for reviews in list
     */
    protected static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView ratingTextView;
        private TextView descriptionTextView;
        private TextView reviewerTextView;
        private TextView locationTextView;

        public ReviewViewHolder(View view) {
            super(view);

            this.ratingTextView = (TextView) view.findViewById(R.id.review_rating);
            this.descriptionTextView = (TextView) view.findViewById(R.id.review_description);
            this.reviewerTextView = (TextView) view.findViewById(R.id.reviewer);
            this.locationTextView = (TextView) view.findViewById(R.id.reviewer_location);
        }

        public void setReview(@NonNull Review review) {
            Preconditions.checkNotNull(review, "Review cannot be null.");

            this.ratingTextView.setText(String.format("%.1f", review.getRating()));
            this.descriptionTextView.setText(review.getDescription());
            this.reviewerTextView.setText(String.format("%s @ %s", review.getReviewer(), review.getDate()));
            this.locationTextView.setText(review.getLocation());

            if (review.getLocation().isEmpty()) {
                this.locationTextView.setVisibility(View.GONE);
            }
        }

        public void clear() {
            this.descriptionTextView.setText("");
        }
    }
}