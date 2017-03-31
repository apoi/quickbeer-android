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

import java.util.Collections;
import java.util.List;

import io.reark.reark.data.DataStreamNotification;
import ix.Ix;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.providers.ProgressStatusProvider;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public abstract class BeerListViewModel extends NetworkViewModel<ItemList<String>> {

    @NonNull
    private final DataLayer.GetBeer getBeer;

    @NonNull
    private final ProgressStatusProvider progressStatusProvider;

    @NonNull
    private final PublishSubject<List<BeerViewModel>> beers = PublishSubject.create();

    protected BeerListViewModel(@NonNull DataLayer.GetBeer getBeer,
                                @NonNull ProgressStatusProvider progressStatusProvider) {
        this.getBeer = get(getBeer);
        this.progressStatusProvider = get(progressStatusProvider);
    }

    @NonNull
    protected abstract Observable<DataStreamNotification<ItemList<String>>> sourceObservable();

    protected boolean reportsProgress() {
        return true;
    }

    @NonNull
    public Observable<List<BeerViewModel>> getBeers() {
        return beers.asObservable();
    }

    @Override
    protected void bind(@NonNull CompositeSubscription subscription) {
        ConnectableObservable<DataStreamNotification<ItemList<String>>> sharedObservable =
                sourceObservable()
                        .subscribeOn(Schedulers.computation())
                        .publish();

        // Construct progress status. Completed with value means we'll receive onNext.
        subscription.add(sharedObservable
                .filter(notification -> !notification.isCompletedWithValue())
                .map(toProgressStatus())
                .startWith(ProgressStatus.LOADING)
                .distinctUntilChanged()
                .subscribe(this::setProgressStatus));

        // Clear list on fetching start
        subscription.add(sharedObservable
                .filter(DataStreamNotification::isOngoing)
                .map(__ -> Collections.<BeerViewModel>emptyList())
                .subscribe(beers::onNext));

        // Actual update
        subscription.add(sharedObservable
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .doOnNext(beerSearch -> Timber.d("Search finished"))
                .map(ItemList::getItems)
                .map(toBeerViewModelList())
                .doOnNext(list -> Timber.d("Publishing " + list.size() + " beers"))
                .subscribe(beers::onNext));

        // Share progress status to progress provider
        if (reportsProgress()) {
            progressStatusProvider.addProgressObservable(sharedObservable
                    .map(notification -> notification));
        }

        subscription.add(sharedObservable.connect());
    }

    @NonNull
    private Func1<List<Integer>, List<BeerViewModel>> toBeerViewModelList() {
        return beerIds -> Ix.from(beerIds)
                .map(integer -> new BeerViewModel(getBeer, integer))
                .toList();
    }

    @Override
    protected boolean hasValue(@Nullable ItemList<String> item) {
        return !get(item).getItems().isEmpty();
    }
}
