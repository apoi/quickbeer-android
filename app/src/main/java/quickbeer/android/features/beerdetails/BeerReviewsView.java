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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.List;

import quickbeer.android.R;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.listeners.LoadMoreListener;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BeerReviewsView extends FrameLayout {

    private BeerReviewsAdapter beerReviewsAdapter;

    private RecyclerView beerReviewsListView;

    public BeerReviewsView(Context context) {
        super(context);
    }

    public BeerReviewsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setReviews(@NonNull List<Review> reviews) {
        get(beerReviewsAdapter).setReviews(get(reviews));
    }

    public void setOnScrollListener(@NonNull LoadMoreListener listener) {
        checkNotNull(beerReviewsListView);
        checkNotNull(listener);

        listener.setLayoutManager((LinearLayoutManager) beerReviewsListView.getLayoutManager());
        beerReviewsListView.addOnScrollListener(listener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        beerReviewsAdapter = new BeerReviewsAdapter();

        beerReviewsListView = (RecyclerView) findViewById(R.id.beers_reviews_list_view);
        beerReviewsListView.setHasFixedSize(true);
        beerReviewsListView.setLayoutManager(new LinearLayoutManager(getContext()));
        beerReviewsListView.setAdapter(beerReviewsAdapter);
    }
}
