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
import android.widget.TextView;

import quickbeer.android.R;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.utils.Score;
import quickbeer.android.viewmodels.BeerViewModel;
import quickbeer.android.views.viewbinders.BeerViewBinder;

import static io.reark.reark.utils.Preconditions.get;

/**
 * View holder for beer list items
 */
public class BeerViewHolder extends RecyclerView.ViewHolder {
    private BeerViewModel viewModel;
    private BeerViewBinder viewBinder;
    private final TextView ratingTextView;
    private final TextView nameTextView;
    private final TextView styleTextView;
    private final TextView brewerTextView;

    public BeerViewHolder(View view, View.OnClickListener onClickListener) {
        super(view);

        this.ratingTextView = (TextView) view.findViewById(R.id.beer_stars);
        this.nameTextView = (TextView) view.findViewById(R.id.beer_name);
        this.styleTextView = (TextView) view.findViewById(R.id.beer_style);
        this.brewerTextView = (TextView) view.findViewById(R.id.brewer_name);

        view.setOnClickListener(onClickListener);
    }

    public void bind(@NonNull final BeerViewModel viewModel) {
        clear();

        this.viewModel = get(viewModel);
        this.viewModel.subscribeToDataStore();

        this.viewBinder = new BeerViewBinder(this, viewModel);
        this.viewBinder.bind();
    }

    public void unbind() {
        if (this.viewBinder != null) {
            this.viewBinder.unbind();
            this.viewBinder = null;
        }

        if (this.viewModel != null) {
            this.viewModel.unsubscribeFromDataStore();
            this.viewModel.dispose();
            this.viewModel = null;
        }
    }

    public void setBeer(@NonNull final Beer beer) {
        String rating = get(beer).rating() >= 0
                ? String.valueOf(beer.rating())
                : "?";

        if (beer.getTickValue() > 0) {
            ratingTextView.setText("");
            ratingTextView.setBackgroundResource(Score.fromTick(beer.getTickValue()).getResource());
        } else {
            ratingTextView.setText(Score.fromRating(beer.rating()));
            ratingTextView.setBackgroundResource(R.drawable.score_unrated);
        }

        nameTextView.setText(beer.name());
        styleTextView.setText(beer.styleName());
        brewerTextView.setText(beer.brewerName());
    }

    public void clear() {
        ratingTextView.setBackground(null);
        ratingTextView.setText("");
        nameTextView.setText("");
        styleTextView.setText("");
        brewerTextView.setText("");
    }
}