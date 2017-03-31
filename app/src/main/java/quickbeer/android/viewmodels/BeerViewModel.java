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
import quickbeer.android.providers.ProgressStatusProvider;
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
    private final ProgressStatusProvider progressStatusProvider;

    @NonNull
    private final BehaviorSubject<Beer> beer = BehaviorSubject.create();

    private int beerId;

    private boolean fullDetails = false;

    public BeerViewModel(@NonNull DataLayer.GetBeer getBeer,
                         @NonNull ProgressStatusProvider progressStatusProvider,
                         boolean fullDetails) {
        this.getBeer = get(getBeer);
        this.progressStatusProvider = get(progressStatusProvider);
        this.fullDetails = fullDetails;
    }

    public BeerViewModel(@NonNull DataLayer.GetBeer getBeer,
                         @NonNull ProgressStatusProvider progressStatusProvider,
                         int beerId) {
        this.beerId = beerId;
        this.progressStatusProvider = get(progressStatusProvider);
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
        ConnectableObservable<DataStreamNotification<Beer>> beerSource =
                getBeer.call(beerId, fullDetails)
                        .subscribeOn(Schedulers.computation())
                        .publish();

        subscription.add(beerSource
                .map(toProgressStatus())
                .subscribe(this::setProgressStatus, Timber::e));

        subscription.add(beerSource
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .subscribe(beer::onNext, Timber::e));

        progressStatusProvider.addProgressObservable(beerSource
                .map(notification -> notification));

        subscription.add(beerSource
                .connect());
    }

    @Override
    protected boolean hasValue(@Nullable Beer item) {
        return true;
    }
}
