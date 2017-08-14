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
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import quickbeer.android.R;
import quickbeer.android.core.activity.InjectingDrawerActivity;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.providers.ToastProvider;
import quickbeer.android.utils.StringUtils;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;
import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * View holder for reviews in list
 */
public class BeerReviewsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.review_details_row)
    View detailsRow;

    @BindView(R.id.review_appearance)
    TextView appearance;

    @BindView(R.id.review_appearance_column)
    View appearanceColumn;

    @BindView(R.id.review_aroma)
    TextView aroma;

    @BindView(R.id.review_aroma_column)
    View aromaColumn;

    @BindView(R.id.review_flavor)
    TextView flavor;

    @BindView(R.id.review_flavor_column)
    View flavorColumn;

    @BindView(R.id.review_mouthfeel)
    TextView mouthfeel;

    @BindView(R.id.review_mouthfeel_column)
    View mouthfeelColumn;

    @BindView(R.id.review_overall)
    TextView overall;

    @BindView(R.id.review_overall_column)
    View overallColumn;

    @BindView(R.id.review_description)
    TextView description;

    @BindView(R.id.review_user)
    TextView user;

    @BindView(R.id.review_location)
    TextView location;

    @Nullable
    @Inject
    ToastProvider toastProvider;

    public BeerReviewsViewHolder(View view) {
        super(view);

        init(view);
    }

    private void init(View view) {
        ButterKnife.bind(this, view);

        ((InjectingDrawerActivity) view.getContext())
                .getComponent()
                .inject(this);
    }

    public void setReview(@NonNull Review review) {
        checkNotNull(review);

        description.setText(review.comments());
        user.setText(format("%s @ %s", review.userName(), review.getDate()));

        if (StringUtils.hasValue(review.country())) {
            location.setText(review.country());
            location.setVisibility(View.VISIBLE);
        } else {
            location.setVisibility(View.GONE);
        }

        if (review.appearance() != null) {
            setDetails(review);
            detailsRow.setVisibility(View.VISIBLE);
        } else {
            detailsRow.setVisibility(View.GONE);
        }
    }

    private void setDetails(@NonNull Review review) {
        appearance.setText(valueOf(review.appearance()));
        aroma.setText(valueOf(review.aroma()));
        flavor.setText(valueOf(review.flavor()));
        mouthfeel.setText(valueOf(review.mouthfeel()));
        overall.setText(valueOf(review.overall()));

        appearanceColumn.setOnClickListener(__ ->
                showToast(R.string.review_appearance_hint));

        aromaColumn.setOnClickListener(__ ->
                showToast(R.string.review_aroma_hint));

        flavorColumn.setOnClickListener(__ ->
                showToast(R.string.review_flavor_hint));

        mouthfeelColumn.setOnClickListener(__ ->
                showToast(R.string.review_mouthfeel_hint));

        overallColumn.setOnClickListener(__ ->
                showToast(R.string.review_overall_hint));
    }

    private void showToast(@StringRes int resource) {
        get(toastProvider).showCancelableToast(resource, Toast.LENGTH_LONG);
    }
}