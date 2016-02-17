package quickbeer.android.next.viewmodels;

import android.support.annotation.NonNull;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.pojo.ReviewList;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class ReviewListViewModel extends BaseViewModel {
    private static final String TAG = ReviewListViewModel.class.getSimpleName();

    @NonNull
    private final DataLayer.GetReviews getReviews;

    private final BehaviorSubject<ReviewList> reviews = BehaviorSubject.create();
    private final int beerId;

    public ReviewListViewModel(final int beerId, @NonNull DataLayer.GetReviews getReviews) {
        Preconditions.checkNotNull(getReviews, "GetReviews cannot be null.");

        this.getReviews = getReviews;
        this.beerId = beerId;
    }

    @NonNull
    public Observable<ReviewList> getReviews() {
        return reviews.asObservable();
    }

    @Override
    public void subscribeToDataStoreInternal(@NonNull CompositeSubscription compositeSubscription) {
        Log.v(TAG, "subscribeToDataStoreInternal");

        ConnectableObservable<DataStreamNotification<ReviewList>> reviewSource =
                getReviews.call(beerId).publish();

        compositeSubscription.add(reviewSource
                .map(toProgressStatus())
                .subscribe(this::setNetworkStatusText));

        compositeSubscription.add(reviewSource
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .doOnNext(beerSearch -> Log.d(TAG, "Review get finished"))
                .subscribe(reviews::onNext));

        compositeSubscription.add(reviewSource
                .connect());
    }
}
