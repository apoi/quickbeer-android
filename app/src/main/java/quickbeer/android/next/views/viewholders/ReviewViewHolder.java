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
package quickbeer.android.next.views.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.R;
import quickbeer.android.next.pojo.Review;

/**
 * View holder for reviews in list
 */
public class ReviewViewHolder extends RecyclerView.ViewHolder {
    private final TextView ratingTextView;
    private final TextView descriptionTextView;
    private final TextView reviewerTextView;
    private final TextView locationTextView;

    public ReviewViewHolder(View view) {
        super(view);

        this.ratingTextView = (TextView) view.findViewById(R.id.review_rating);
        this.descriptionTextView = (TextView) view.findViewById(R.id.review_description);
        this.reviewerTextView = (TextView) view.findViewById(R.id.reviewer);
        this.locationTextView = (TextView) view.findViewById(R.id.reviewer_location);
    }

    public void setReview(@NonNull final Review review) {
        Preconditions.checkNotNull(review, "Review cannot be null.");

        this.ratingTextView.setText(String.format(Locale.ROOT, "%.1f", review.totalScore()));
        this.descriptionTextView.setText(review.comments());
        this.reviewerTextView.setText(String.format("%s @ %s", review.userName(), review.getDate()));
        this.locationTextView.setText(review.getLocation());

        if (review.getLocation().isEmpty()) {
            this.locationTextView.setVisibility(View.GONE);
        }
    }

    public void clear() {
        this.descriptionTextView.setText("");
    }
}