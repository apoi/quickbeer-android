package quickbeer.android.next.viewmodels;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.pojo.BeerSearch;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by antti on 25.10.2015.
 */
public class BeerListViewModel extends BaseViewModel {
    private static final String TAG = BeerListViewModel.class.getSimpleName();

    @NonNull
    private final DataLayer.GetTopBeers getTopBeers;
    private final DataLayer.GetBeer getBeer;

    private final PublishSubject<Integer> selectBeer = PublishSubject.create();
    private final BehaviorSubject<List<BeerViewModel>> beers = BehaviorSubject.create();

    public BeerListViewModel(@NonNull DataLayer.GetTopBeers getTopBeers,
                             @NonNull DataLayer.GetBeer getBeer) {
        Preconditions.checkNotNull(getTopBeers, "GetTopBeers cannot be null.");

        this.getTopBeers = getTopBeers;
        this.getBeer = getBeer;
    }

    @NonNull
    public Observable<Integer> getSelectBeer() {
        return selectBeer.asObservable();
    }

    public void selectBeer(final int beerId) {
        this.selectBeer.onNext(beerId);
    }

    @NonNull
    public Observable<List<BeerViewModel>> getBeers() {
        return beers.asObservable();
    }

    @Override
    public void subscribeToDataStoreInternal(@NonNull CompositeSubscription compositeSubscription) {
        Log.v(TAG, "subscribeToDataStoreInternal");

        ConnectableObservable<DataStreamNotification<BeerSearch>> beerSearchSource =
                getTopBeers.call().publish();

        compositeSubscription.add(beerSearchSource
                .map(toProgressStatus())
                .subscribe(this::setNetworkStatusText));

        compositeSubscription.add(beerSearchSource
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .doOnNext(beerSearch -> Log.d(TAG, "Search finished"))
                .map(BeerSearch::getItems)
                .flatMap(toBeerViewModelList())
                .doOnNext(list -> Log.d(TAG, "Publishing " + list.size() + " beers from the view model"))
                .subscribe(beers::onNext));

        compositeSubscription.add(beerSearchSource
                .connect());
    }

    @NonNull
    Func1<List<Integer>, Observable<List<BeerViewModel>>> toBeerViewModelList() {
        return beerIds -> Observable.from(beerIds)
                .map(integer -> new BeerViewModel(integer, getBeer))
                .toList();
    }
}