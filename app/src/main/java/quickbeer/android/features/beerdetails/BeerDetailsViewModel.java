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

import java.util.List;

import quickbeer.android.core.viewmodel.SimpleViewModel;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.viewmodels.BeerViewModel;
import quickbeer.android.viewmodels.ReviewListViewModel;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static io.reark.reark.utils.Preconditions.get;

public class BeerDetailsViewModel extends SimpleViewModel {

    @NonNull
    private final BeerViewModel beerViewModel;

    @NonNull
    private final ReviewListViewModel reviewListViewModel;

    public BeerDetailsViewModel(@NonNull DataLayer.GetBeer getBeer,
                                @NonNull DataLayer.GetReviews getReviews,
                                @NonNull DataLayer.GetReview getReview) {
        beerViewModel = new BeerViewModel(get(getBeer));
        reviewListViewModel = new ReviewListViewModel(get(getReviews), get(getReview));
    }

    public void setBeerId(int beerId) {
        beerViewModel.setBeerId(beerId);
        reviewListViewModel.setBeerId(beerId);
    }

    @NonNull
    public Observable<Beer> getBeer() {
        return beerViewModel.getBeer();
    }

    @NonNull
    public Observable<List<Review>> getReviews() {
        return reviewListViewModel.getReviews();
    }

    @Override
    protected void bind(@NonNull CompositeSubscription subscription) {
        beerViewModel.bindToDataModel();
        reviewListViewModel.bindToDataModel();
    }

    @Override
    protected void unbind() {
        beerViewModel.unbindDataModel();
        reviewListViewModel.unbindDataModel();
    }
}
