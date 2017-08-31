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
package quickbeer.android.viewmodels;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.RxUtils;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.providers.ProgressStatusProvider;
import quickbeer.android.rx.Unit;
import rx.Observable;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class ReviewListViewModel extends NetworkViewModel<ItemList<Review>> {

    @NonNull
    private final DataLayer.GetReviews getReviews;

    @NonNull
    private final DataLayer.FetchReviews fetchReviews;

    @NonNull
    private final DataLayer.GetReview getReview;

    @NonNull
    private final ReviewListStore reviewListStore;

    @NonNull
    private final ProgressStatusProvider progressStatusProvider;

    @NonNull
    private final PublishSubject<Unit> loadTrigger = PublishSubject.create();

    @NonNull
    private final PublishSubject<Unit> reloadTrigger = PublishSubject.create();

    @NonNull
    private final BehaviorSubject<List<Review>> reviews = BehaviorSubject.create();

    private final int beerId;

    public ReviewListViewModel(int beerId,
                               @NonNull DataLayer.GetReviews getReviews,
                               @NonNull DataLayer.FetchReviews fetchReviews,
                               @NonNull DataLayer.GetReview getReview,
                               @NonNull ReviewListStore reviewListStore,
                               @NonNull ProgressStatusProvider progressStatusProvider) {
        this.beerId = beerId;
        this.getReviews = get(getReviews);
        this.fetchReviews = get(fetchReviews);
        this.getReview = get(getReview);
        this.reviewListStore = get(reviewListStore);
        this.progressStatusProvider = get(progressStatusProvider);
    }

    @NonNull
    public Observable<List<Review>> getReviews() {
        return reviews.asObservable();
    }

    public void fetchReviews(int page) {
        Timber.w("fetchReviews(%s)", page);
        fetchReviews.call(beerId, page);
    }

    public void reloadReviews() {
        reloadTrigger.onNext(Unit.DEFAULT);
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
        return getReview
                .call(get(reviewId))
                .compose(quickbeer.android.rx.RxUtils::pickValue)
                .doOnNext((review) -> Timber.v("Received review " + review.id()));
    }

    @Override
    protected void bind(@NonNull CompositeSubscription subscription) {
        Timber.v("subscribeToDataStoreInternal");

        ConnectableObservable<DataStreamNotification<ItemList<Integer>>> reviewSource =
                loadTrigger.flatMap(__ -> getReviews.call(beerId))
                        .publish();

        subscription.add(reviewSource
                .map(toStaticProgressStatus())
                .subscribe(this::setProgressStatus));

        subscription.add(reviewSource
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .doOnNext(search -> Timber.d("Review get finished"))
                .map(ItemList<Integer>::getItems)
                .flatMap(toReviewList())
                .doOnNext(list -> Timber.d("Publishing " + list.size() + " reviews from the view model"))
                .subscribe(reviews::onNext));

        subscription.add(progressStatusProvider
                .addProgressObservable(reviewSource
                        .map(notification -> notification)));

        subscription.add(reviewSource
                .connect());

        subscription.add(reloadTrigger.asObservable()
                .switchMap(__ -> reviewListStore.delete(beerId).toObservable())
                .doOnEach(__ -> reviews.onNext(Collections.emptyList()))
                .doOnEach(__ -> loadTrigger.onNext(Unit.DEFAULT))
                .subscribe(Actions.empty(), Timber::e));

        // Initial trigger at bind time
        loadTrigger.onNext(Unit.DEFAULT);
    }

    @Override
    protected boolean hasValue(@Nullable ItemList<Review> item) {
        return !get(item).getItems().isEmpty();
    }
}
