package quickbeer.android.next.viewmodels;

import android.support.annotation.NonNull;

import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.pojo.Review;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class ReviewViewModel  extends BaseViewModel {
    private static final String TAG = ReviewViewModel.class.getSimpleName();

    @NonNull
    private final DataLayer.GetReview getReview;

    private final BehaviorSubject<Review> review = BehaviorSubject.create();
    private final int reviewId;

    public ReviewViewModel(final int reviewId, @NonNull DataLayer.GetReview getReview) {
        Preconditions.checkNotNull(getReview, "GetReview cannot be null.");

        this.getReview = getReview;
        this.reviewId = reviewId;
    }

    @NonNull
    public Observable<Review> getReview() {
        return review.asObservable();
    }

    @Override
    public void subscribeToDataStoreInternal(@NonNull CompositeSubscription compositeSubscription) {
        Log.v(TAG, "subscribeToDataStoreInternal");

        ConnectableObservable<Review> reviewSource =
                getReview.call(reviewId).publish();

        compositeSubscription.add(reviewSource
                .doOnNext(review -> Log.d(TAG, "Review get finished"))
                .subscribe(review::onNext));

        compositeSubscription.add(reviewSource
                .connect());
    }
}

