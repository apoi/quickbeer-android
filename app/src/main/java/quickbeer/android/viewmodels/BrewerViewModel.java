/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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
import quickbeer.android.data.pojos.Brewer;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class BrewerViewModel extends NetworkViewModel<Brewer> {

    @NonNull
    private final DataLayer.GetBrewer getBrewer;

    @NonNull
    private final BehaviorSubject<Brewer> brewer = BehaviorSubject.create();

    private int brewerId;

    public BrewerViewModel(@NonNull DataLayer.GetBrewer getBrewer) {
        this.getBrewer = get(getBrewer);
    }

    public BrewerViewModel(int brewerId, @NonNull DataLayer.GetBrewer getBrewer) {
        this.brewerId = brewerId;
        this.getBrewer = get(getBrewer);
    }

    public int getBrewerId() {
        return brewerId;
    }

    public void setBrewerId(int brewerId) {
        this.brewerId = brewerId;
    }

    @NonNull
    public Observable<Brewer> getBrewer() {
        return brewer.asObservable();
    }

    @Override
    protected void bind(@NonNull CompositeSubscription subscription) {
        ConnectableObservable<DataStreamNotification<Brewer>> brewerSource =
                getBrewer.call(brewerId)
                        .subscribeOn(Schedulers.computation())
                        .publish();

        subscription.add(brewerSource
                .map(toProgressStatus())
                .subscribe(this::setProgressStatus, Timber::e));

        subscription.add(brewerSource
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .subscribe(brewer::onNext, Timber::e));

        subscription.add(brewerSource
                .connect());
    }

    @Override
    protected boolean hasValue(@Nullable Brewer item) {
        return true;
    }
}
