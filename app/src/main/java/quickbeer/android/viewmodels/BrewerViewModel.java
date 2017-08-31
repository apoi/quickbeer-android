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

import javax.inject.Inject;
import javax.inject.Named;

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.providers.ProgressStatusProvider;
import rx.Observable;
import rx.functions.Actions;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class BrewerViewModel extends NetworkViewModel<Brewer> {

    @NonNull
    private final DataLayer.GetBeer getBeer;

    @NonNull
    private final DataLayer.GetBrewer getBrewer;

    @NonNull
    private final DataLayer.GetBrewer reloadBrewer;

    @NonNull
    private final ProgressStatusProvider progressStatusProvider;

    @NonNull
    private final BehaviorSubject<Brewer> brewer = BehaviorSubject.create();

    @NonNull
    private final CompositeSubscription subscription = new CompositeSubscription();

    private final int brewerId;

    private final int beerId;

    @Inject
    public BrewerViewModel(@Named("id") @NonNull Integer brewerId,
                           @Named("secondaryId") Integer beerId,
                           @NonNull DataLayer.GetBeer getBeer,
                           @NonNull DataLayer.GetBrewer getBrewer,
                           @Named("reload") @NonNull DataLayer.GetBrewer reloadBrewer,
                           @NonNull ProgressStatusProvider progressStatusProvider) {
        this.brewerId = brewerId;
        this.beerId = beerId;
        this.getBeer = get(getBeer);
        this.getBrewer = get(getBrewer);
        this.reloadBrewer = get(reloadBrewer);
        this.progressStatusProvider = get(progressStatusProvider);
    }

    public BrewerViewModel(@NonNull Integer brewerId,
                           @NonNull DataLayer.GetBeer getBeer,
                           @NonNull DataLayer.GetBrewer getBrewer,
                           @NonNull DataLayer.GetBrewer reloadBrewer,
                           @NonNull ProgressStatusProvider progressStatusProvider) {
        this.brewerId = brewerId;
        this.beerId = -1;
        this.getBeer = get(getBeer);
        this.getBrewer = get(getBrewer);
        this.reloadBrewer = get(reloadBrewer);
        this.progressStatusProvider = get(progressStatusProvider);
    }

    public int getBrewerId() {
        return brewerId;
    }

    @NonNull
    public Observable<Brewer> getBrewer() {
        return brewer.asObservable();
    }

    public void reloadBrewerDetails() {
        subscription.add(reloadBrewer.call(brewerId)
                .subscribe(Actions.empty(), Timber::e));
    }

    @Override
    protected void bind(@NonNull CompositeSubscription subscription) {
        ConnectableObservable<DataStreamNotification<Brewer>> brewerSource =
                brewerSource()
                        .subscribeOn(Schedulers.computation())
                        .publish();

        subscription.add(brewerSource
                .map(toProgressStatus())
                .subscribe(this::setProgressStatus, Timber::e));

        subscription.add(brewerSource
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .subscribe(brewer::onNext, Timber::e));

        subscription.add(progressStatusProvider
                .addProgressObservable(brewerSource
                        .map(notification -> notification)));

        subscription.add(brewerSource
                .connect());
    }

    @NonNull
    private Observable<DataStreamNotification<Brewer>> brewerSource() {
        if (brewerId > 0) {
            return getBrewer.call(brewerId);
        } else if (beerId > 0) {
            return getBeer.call(beerId)
                    .filter(DataStreamNotification::isOnNext)
                    .map(DataStreamNotification::getValue)
                    .map(Beer::brewerId)
                    .flatMap(getBrewer::call);
        } else {
            throw new IllegalStateException("No source id!");
        }
    }

    @Override
    protected boolean hasValue(@Nullable Brewer item) {
        return true;
    }

    @Override
    protected void unbind() {
        subscription.clear();
    }
}
