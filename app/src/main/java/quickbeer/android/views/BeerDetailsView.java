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
package quickbeer.android.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.List;

import io.reark.reark.utils.RxViewBinder;
import quickbeer.android.R;
import quickbeer.android.adapters.BeerDetailsAdapter;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.viewmodels.BeerViewModel;
import quickbeer.android.viewmodels.ReviewListViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static io.reark.reark.utils.Preconditions.get;

public class BeerDetailsView extends FrameLayout {
    private BeerDetailsAdapter beerDetailsAdapter;

    public BeerDetailsView(Context context) {
        super(context);
    }

    public BeerDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void setBeer(@NonNull final Beer beer) {
        get(beerDetailsAdapter).setBeer(get(beer));
    }

    private void setReviews(@NonNull final List<Review> reviews) {
        get(beerDetailsAdapter).setReviews(get(reviews));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        beerDetailsAdapter = new BeerDetailsAdapter();

        RecyclerView beerDetailsListView = (RecyclerView) findViewById(R.id.beers_details_list_view);
        beerDetailsListView.setHasFixedSize(true);
        beerDetailsListView.setLayoutManager(new LinearLayoutManager(getContext()));
        beerDetailsListView.setAdapter(beerDetailsAdapter);
    }

    /**
     * View binder between BeerViewModel and the BeerDetailsView
     */
    public static class BeerViewBinder extends RxViewBinder {
        private final BeerDetailsView view;
        private final BeerViewModel viewModel;

        public BeerViewBinder(@NonNull final BeerDetailsView view,
                              @NonNull final BeerViewModel viewModel) {
            this.view = get(view);
            this.viewModel = get(viewModel);
        }

        @Override
        protected void bindInternal(@NonNull final CompositeSubscription subscription) {
            subscription.add(viewModel.getBeer()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setBeer));
        }
    }

    /**
     * View binder between ReviewListViewModel and the BeerDetailsView
     */
    public static class ReviewListViewBinder extends RxViewBinder {
        private final BeerDetailsView view;
        private final ReviewListViewModel viewModel;

        public ReviewListViewBinder(@NonNull final BeerDetailsView view,
                                    @NonNull final ReviewListViewModel viewModel) {
            this.view = get(view);
            this.viewModel = get(viewModel);
        }

        @Override
        protected void bindInternal(@NonNull final CompositeSubscription subscription) {
            subscription.add(viewModel.getReviews()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setReviews));
        }
    }
}
