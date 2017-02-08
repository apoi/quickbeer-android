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
package quickbeer.android.views.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import quickbeer.android.R;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.transformations.ContainerLabelExtractor;
import quickbeer.android.utils.Score;
import quickbeer.android.utils.StringUtils;

import static io.reark.reark.utils.Preconditions.checkNotNull;

/**
 * View holder for all the beer details
 */
public class BeerDetailsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.beer_stars)
    TextView ratingTextView;

    @BindView(R.id.beer_name)
    TextView nameTextView;

    @BindView(R.id.beer_style)
    TextView styleTextView;

    @BindView(R.id.beer_abv)
    TextView abvTextView;

    @BindView(R.id.brewer_name)
    TextView brewerTextView;

    @BindView(R.id.brewer_location)
    TextView locationTextView;

    @BindView(R.id.beer_description)
    TextView descriptionTextView;

    @BindView(R.id.beer_details_image)
    ImageView imageView;

    public BeerDetailsViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void setBeer(@NonNull final Beer beer) {
        checkNotNull(beer);

        if (beer.getTickValue() > 0) {
            ratingTextView.setText("");
            ratingTextView.setBackgroundResource(Score.fromTick(beer.getTickValue()).getResource());
        } else {
            ratingTextView.setText(Score.fromRating(beer.rating()));
            ratingTextView.setBackgroundResource(R.drawable.score_unrated);
        }

        nameTextView.setText(beer.name());
        styleTextView.setText(beer.styleName());
        abvTextView.setText(String.format(Locale.ROOT, "ABV: %.1f%%", beer.getAbv()));
        brewerTextView.setText(beer.brewerName());
        locationTextView.setText("TODO data from brewer");
        descriptionTextView.setText(StringUtils.value(beer.description(), "No description available."));

        imageView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            @Override
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