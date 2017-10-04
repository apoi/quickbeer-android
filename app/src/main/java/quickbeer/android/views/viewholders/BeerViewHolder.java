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
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import quickbeer.android.R;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.core.viewmodel.viewholder.BaseBindingViewHolder;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.utils.Score;
import quickbeer.android.utils.StringUtils;
import quickbeer.android.viewmodels.BeerViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class BeerViewHolder extends BaseBindingViewHolder<BeerViewModel> {

    @BindView(R.id.beer_stars)
    TextView ratingTextView;

    @BindView(R.id.beer_name)
    TextView nameTextView;

    @BindView(R.id.beer_style)
    TextView styleTextView;

    @BindView(R.id.brewer_name)
    TextView brewerTextView;

    @NonNull
    private final DataBinder viewDataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
            clear();

            subscription.add(get(getViewModel())
                    .getBeer()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(BeerViewHolder.this::setBeer, Timber::e));
        }
    };

    public BeerViewHolder(@NonNull View view,
                          @NonNull View.OnClickListener onClickListener) {
        super(view);

        ButterKnife.bind(this, view);
        view.setOnClickListener(onClickListener);
    }

    @Override
    @NonNull
    public DataBinder getViewDataBinder() {
        return viewDataBinder;
    }

    public void setBeer(@NonNull Beer beer) {
        // Don't show anything if the name isn't loaded yet.
        // This prevents the rating label to be shown with empty details.
        if (StringUtils.isEmpty(beer.getName())) {
            return;
        }

        if (beer.isTicked()) {
            ratingTextView.setText("");
            ratingTextView.setBackgroundResource(Score.fromTick(beer.getTickValue()).getResource());
        } else {
            ratingTextView.setText(Score.fromRating(beer.rating()));
            ratingTextView.setBackgroundResource(R.drawable.score_unrated);
        }

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