package quickbeer.android.next.viewmodels;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.data.DataStreamNotification;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.BeerSearch;
import quickbeer.android.next.utils.Preconditions;
import quickbeer.android.next.utils.RxUtils;
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

    private final PublishSubject<Beer> selectBeer = PublishSubject.create();
    private final BehaviorSubject<List<Beer>> beers = BehaviorSubject.create();

    public BeerListViewModel(@NonNull DataLayer.GetTopBeers getTopBeers,
                             @NonNull DataLayer.GetBeer getBeer) {
        Preconditions.checkNotNull(getTopBeers, "GetTopBeers cannot be null.");

        this.getTopBeers = getTopBeers;
        this.getBeer = getBeer;
    }

    @NonNull
    public Observable<Beer> getSelectBeer() {
        return selectBeer.asObservable();
    }

    public void selectBeer(@NonNull Beer beer) {
        Preconditions.checkNotNull(beer, "Beer cannot be null.");

        this.selectBeer.onNext(beer);
    }

    @NonNull
    public Observable<List<Beer>> getBeers() {
        return beers.asObservable();
    }

    @Override
    protected void subscribeToDataStoreInternal(@NonNull CompositeSubscription compositeSubscription) {
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
                .flatMap(toBeerList())
                .doOnNext(list -> Log.d(TAG, "Publishing " + list.size() + " beers from the view model"))
                .subscribe(beers::onNext));

        compositeSubscription.add(beerSearchSource
                .connect());
    }

    @NonNull
    Func1<List<Integer>, Observable<List<Beer>>> toBeerList() {
        return repositoryIds -> Observable.from(repositoryIds)
                .map(this::getBeerObservable)
                .toList()
                .flatMap(RxUtils::toObservableList);
    }

    @NonNull
    Observable<Beer> getBeerObservable(@NonNull Integer beerId) {
        Preconditions.checkNotNull(beerId, "Beer id cannot be null.");

        return getBeer.call(beerId)
                .doOnNext((beer) -> Log.v(TAG, "Received beer " + beer.getId()));
    }
}