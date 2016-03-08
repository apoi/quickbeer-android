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
package quickbeer.android.next.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.R;
import quickbeer.android.next.activities.BeerDetailsActivity;
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.viewmodels.BeerViewModel;
import quickbeer.android.next.viewmodels.ReviewListViewModel;
import quickbeer.android.next.views.BeerDetailsView;

public class BeerDetailsFragment extends Fragment {
    private static final String TAG = BeerDetailsFragment.class.getSimpleName();

    private BeerViewModel beerViewModel;
    private BeerDetailsView.BeerViewBinder beerViewBinder;

    private ReviewListViewModel reviewListViewModel;
    private BeerDetailsView.ReviewListViewBinder reviewListViewBinder;

    @Inject
    DataLayer.GetBeer getBeer;

    @Inject
    DataLayer.GetReviews getReviews;

    @Inject
    DataLayer.GetReview getReview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QuickBeer.getInstance().getGraph().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.beer_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int beerId = ((BeerDetailsActivity) getActivity()).getBeerId();
        BeerDetailsView detailsView = (BeerDetailsView) view.findViewById(R.id.beer_details_view);

        beerViewModel = new BeerViewModel(beerId, getBeer);
        beerViewModel.subscribeToDataStore();
        beerViewBinder = new BeerDetailsView.BeerViewBinder(detailsView, beerViewModel);

        reviewListViewModel = new ReviewListViewModel(beerId, getReviews, getReview);
        reviewListViewModel.subscribeToDataStore();
        reviewListViewBinder = new BeerDetailsView.ReviewListViewBinder(detailsView, reviewListViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        beerViewBinder.bind();
        reviewListViewBinder.bind();
    }

    @Override
    public void onPause() {
        super.onPause();
        beerViewBinder.unbind();
        reviewListViewBinder.unbind();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        beerViewModel.unsubscribeFromDataStore();
        reviewListViewModel.unsubscribeFromDataStore();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beerViewModel.dispose();
        beerViewModel = null;

        reviewListViewModel.dispose();
        reviewListViewModel = null;
    }
}
