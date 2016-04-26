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
package quickbeer.android.next.viewmodels;

import android.support.annotation.NonNull;

import java.util.List;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxUtils;
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.pojo.Review;
import quickbeer.android.next.pojo.RelationList;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class ReviewListViewModel extends BaseViewModel {
    private static final String TAG = ReviewListViewModel.class.getSimpleName();

    private final DataLayer.GetReviews getReviews;
    private final DataLayer.GetReview getReview;

    private final BehaviorSubject<List<Review>> reviews = BehaviorSubject.create();
    private final int beerId;

    public ReviewListViewModel(final int beerId,
                               @NonNull DataLayer.GetReviews getReviews,
                               @NonNull DataLayer.GetReview getReview) {
        Preconditions.checkNotNull(getReviews, "GetReviews cannot be null.");
        Preconditions.checkNotNull(getReview, "GetReview cannot be null.");

        this.getReviews = getReviews;
        this.getReview = getReview;
        this.beerId = beerId;
    }

    @NonNull
    public Observable<List<Review>> getReviews() {
        return reviews.asObservable();
    }

    @Override
    public void subscribeToDataStoreInternal(@NonNull CompositeSubscription compositeSubscription) {
        Log.v(TAG, "subscribeToDataStoreInternal");

        ConnectableObservable<DataStreamNotification<RelationList>> reviewSource =
                getReviews.call(beerId).publish();

        compositeSubscription.add(reviewSource
                .map(toProgressStatus())
                .subscribe(this::setNetworkStatusText));

        compositeSubscription.add(reviewSource
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .doOnNext(search -> Log.d(TAG, "Review get finished"))
                .map(RelationList::getItems)
                .flatMap(toReviewList())
                .doOnNext(list -> Log.d(TAG, "Publishing " + list.size() + " reviews from the view model"))
                .subscribe(reviews::onNext));

        compositeSubscription.add(reviewSource
                .connect());
    }

    @NonNull
    private Func1<List<Integer>, Observable<List<Review>>> toReviewList() {
        return reviewIds -> Observable.from(reviewIds)
                .map(this::getReviewObservable)
                .toList()
                .flatMap(RxUtils::toObservableList);
    }

    @NonNull
    private Observable<Review> getReviewObservable(@NonNull Integer reviewId) {
        Preconditions.checkNotNull(reviewId, "Review id cannot be null.");

        return getReview
                .call(reviewId)
                .doOnNext((review) -> Log.v(TAG, "Received review " + review.getId()));
    }
}
