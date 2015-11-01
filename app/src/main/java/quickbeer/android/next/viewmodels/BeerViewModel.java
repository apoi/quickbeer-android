package quickbeer.android.next.viewmodels;

import android.support.annotation.NonNull;

import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.pojo.Beer;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by antti on 27.10.2015.
 */
public class BeerViewModel extends BaseViewModel {
    private static final String TAG = BeerViewModel.class.getSimpleName();

    @NonNull
    private final DataLayer.GetBeer getBeer;

    private final BehaviorSubject<Beer> beer = BehaviorSubject.create();
    private final int beerId;

    public BeerViewModel(final int beerId, @NonNull DataLayer.GetBeer getBeer) {
        Preconditions.checkNotNull(getBeer, "GetBeer cannot be null.");

        this.getBeer = getBeer;
        this.beerId = beerId;
    }

    @NonNull
    public Observable<Beer> getBeer() {
        return beer.asObservable();
    }

    @Override
    public void subscribeToDataStoreInternal(@NonNull CompositeSubscription compositeSubscription) {
        Log.v(TAG, "subscribeToDataStoreInternal");

        compositeSubscription.add(getBeer
                .call(beerId)
                .subscribe(beer::onNext));
    }
}
