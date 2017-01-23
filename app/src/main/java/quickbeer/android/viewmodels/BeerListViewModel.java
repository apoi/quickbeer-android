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

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.ItemList;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class BeerListViewModel extends NetworkViewModel {

    @NonNull
    private final DataLayer.GetBeer getBeer;

    @Nullable
    private Observable<DataStreamNotification<ItemList<String>>> sourceObservable;

    @NonNull
    private final PublishSubject<Integer> selectBeer = PublishSubject.create();

    @NonNull
    private final BehaviorSubject<List<BeerViewModel>> beers = BehaviorSubject.create();

    @NonNull
    private final PublishSubject<DataStreamNotification<ItemList<String>>> notificationSubject = PublishSubject.create();

    @Inject
    BeerListViewModel(@NonNull final DataLayer.GetBeer getBeer) {
        this.getBeer = get(getBeer);
    }

    @NonNull
    public Observable<Integer> selectedBeerStream() {
        return selectBeer.asObservable();
    }

    public void selectBeer(final int beerId) {
        this.selectBeer.onNext(beerId);
    }

    @NonNull
    public Observable<List<BeerViewModel>> getBeers() {
        return beers.asObservable();
    }

    public void setNotification(@NonNull final DataStreamNotification<ItemList<String>> notification) {
        notificationSubject.onNext(notification);
    }

    @Override
    protected void bind(@NonNull final CompositeSubscription subscription) {
        Timber.v("bind");

        subscription.add(notificationSubject
                .observeOn(Schedulers.computation())
                .map(toProgressStatus())
                .subscribe(this::setNetworkStatusText));

        subscription.add(notificationSubject
                .observeOn(Schedulers.computation())
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .doOnNext(beerSearch -> Timber.d("Search finished"))
                .map(ItemList::getItems)
                .flatMap(toBeerViewModelList())
                .doOnNext(list -> Timber.d("Publishing " + list.size() + " beers"))
                .subscribe(beers::onNext));
    }

    @NonNull
    private Func1<List<Integer>, Observable<List<BeerViewModel>>> toBeerViewModelList() {
        return beerIds -> Observable.from(beerIds)
                .map(integer -> new BeerViewModel(integer, getBeer))
                .toList();
    }
}
