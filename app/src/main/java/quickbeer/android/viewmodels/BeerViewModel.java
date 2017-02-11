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

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Beer;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class BeerViewModel extends NetworkViewModel<Beer> {

    @NonNull
    private final DataLayer.GetBeer getBeer;

    @NonNull
    private final BehaviorSubject<Beer> beer = BehaviorSubject.create();

    private int beerId;

    public BeerViewModel(@NonNull DataLayer.GetBeer getBeer) {
        this.getBeer = get(getBeer);
    }

    public BeerViewModel(int beerId, @NonNull DataLayer.GetBeer getBeer) {
        this.beerId = beerId;
        this.getBeer = get(getBeer);
    }

    public int getBeerId() {
        return beerId;
    }

    public void setBeerId(int beerId) {
        this.beerId = beerId;
    }

    @NonNull
    public Observable<Beer> getBeer() {
        return beer.asObservable();
    }

    @Override
    protected void bind(@NonNull CompositeSubscription subscription) {
        Timber.v("subscribeToDataStoreInternal");

        ConnectableObservable<DataStreamNotification<Beer>> beerSource =
                Observable.just(beerId)
                        .observeOn(Schedulers.computation())
                        .flatMap(getBeer::call)
                        .publish();

        subscription.add(beerSource
                .map(toProgressStatus())
                .subscribe(this::setProgressStatus, Timber::e));

        subscription.add(beerSource
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .doOnNext(beerSearch -> Timber.d("Beer get finished"))
                .subscribe(beer::onNext, Timber::e));

        subscription.add(beerSource
                .connect());
    }

    @Override
    protected boolean hasValue(@Nullable Beer item) {
        return true;
    }
}
