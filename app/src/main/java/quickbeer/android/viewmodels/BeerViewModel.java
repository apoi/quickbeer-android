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

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Beer;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class BeerViewModel extends BaseViewModel {

    @NonNull
    private final DataLayer.GetBeer getBeer;

    private final BehaviorSubject<Beer> beer = BehaviorSubject.create();
    private final int beerId;

    public BeerViewModel(final int beerId, @NonNull final DataLayer.GetBeer getBeer) {
        this.getBeer = get(getBeer);
        this.beerId = beerId;
    }

    @NonNull
    public Observable<Beer> getBeer() {
        return beer.asObservable();
    }

    @Override
    public void subscribeToDataStoreInternal(@NonNull final CompositeSubscription compositeSubscription) {
        Timber.v("subscribeToDataStoreInternal");

        ConnectableObservable<DataStreamNotification<Beer>> beerSource =
                Observable.just(beerId)
                        .observeOn(Schedulers.computation())
                        .flatMap(getBeer::call)
                        .publish();

        compositeSubscription.add(beerSource
                .map(toProgressStatus())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setNetworkStatusText));

        compositeSubscription.add(beerSource
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .doOnNext(beerSearch -> Timber.d("Beer get finished"))
                .subscribe(beer::onNext));

        compositeSubscription.add(beerSource
                .connect());
    }
}
