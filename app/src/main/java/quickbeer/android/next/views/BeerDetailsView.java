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
package quickbeer.android.next.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.List;

import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxViewBinder;
import quickbeer.android.next.R;
import quickbeer.android.next.adapters.BeerDetailsAdapter;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.Review;
import quickbeer.android.next.viewmodels.BeerViewModel;
import quickbeer.android.next.viewmodels.ReviewListViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class BeerDetailsView extends FrameLayout {
    private BeerDetailsAdapter beerDetailsAdapter;

    public BeerDetailsView(Context context) {
        super(context);
    }

    public BeerDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void setBeer(@NonNull Beer beer) {
        Preconditions.checkNotNull(beer, "Beer cannot be null.");
        Preconditions.checkState(beerDetailsAdapter != null, "Beer details adapter cannot be null.");

        beerDetailsAdapter.setBeer(beer);
    }

    private void setReviews(@NonNull List<Review> reviews) {
        Preconditions.checkNotNull(reviews, "Reviews cannot be null.");
        Preconditions.checkState(beerDetailsAdapter != null, "Beer details adapter cannot be null.");

        beerDetailsAdapter.setReviews(reviews);
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

        public BeerViewBinder(@NonNull BeerDetailsView view,
                              @NonNull BeerViewModel viewModel) {
            Preconditions.checkNotNull(view, "View cannot be null.");
            Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

            this.view = view;
            this.viewModel = viewModel;
        }

        @Override
        protected void bindInternal(@NonNull CompositeSubscription subscription) {
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

        public ReviewListViewBinder(@NonNull BeerDetailsView view,
                                    @NonNull ReviewListViewModel viewModel) {
            Preconditions.checkNotNull(view, "View cannot be null.");
            Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

            this.view = view;
            this.viewModel = viewModel;
        }

        @Override
        protected void bindInternal(@NonNull CompositeSubscription subscription) {
            subscription.add(viewModel.getReviews()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setReviews));
        }
    }
}
