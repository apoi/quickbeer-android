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
package quickbeer.android.features.beer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.data.DataLayer;
import quickbeer.android.core.fragment.BaseFragment;
import quickbeer.android.viewmodels.BeerViewModel;
import quickbeer.android.viewmodels.ReviewListViewModel;
import quickbeer.android.views.BeerDetailsView;

public class BeerDetailsFragment extends BaseFragment {

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
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.beer_details_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int beerId = ((BeerDetailsActivity) getActivity()).getBeerId();
        BeerDetailsView detailsView = (BeerDetailsView) getView().findViewById(R.id.beer_details_view);

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
