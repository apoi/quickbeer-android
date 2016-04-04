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

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.R;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.utils.Score;
import quickbeer.android.next.viewmodels.BeerViewModel;
import quickbeer.android.next.views.viewbinders.BeerViewBinder;

/**
 * View holder for beer list items
 */
public class BeerViewHolder extends RecyclerView.ViewHolder {
    private BeerViewModel viewModel;
    private BeerViewBinder viewBinder;
    private TextView ratingTextView;
    private TextView nameTextView;
    private TextView styleTextView;
    private TextView brewerTextView;

    public BeerViewHolder(View view, View.OnClickListener onClickListener) {
        super(view);

        this.ratingTextView = (TextView) view.findViewById(R.id.beer_stars);
        this.nameTextView = (TextView) view.findViewById(R.id.beer_name);
        this.styleTextView = (TextView) view.findViewById(R.id.beer_style);
        this.brewerTextView = (TextView) view.findViewById(R.id.brewer_name);

        view.setOnClickListener(onClickListener);
    }

    public void bind(BeerViewModel viewModel) {
        Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

        clear();

        this.viewModel = viewModel;
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

    public void setBeer(@NonNull Beer beer) {
        Preconditions.checkNotNull(beer, "Beer cannot be null.");

        String rating = beer.getRating() >= 0
                ? String.valueOf(beer.getRating())
                : "?";

        ratingTextView.setBackgroundResource(Score.Stars.UNRATED.getResource());
        ratingTextView.setText(rating);
        nameTextView.setText(beer.getName());
        styleTextView.setText(beer.getStyleName());
        brewerTextView.setText(beer.getBrewerName());
    }

    public void clear() {
        ratingTextView.setBackground(null);
        ratingTextView.setText("");
        nameTextView.setText("");
        styleTextView.setText("");
        brewerTextView.setText("");
    }
}