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

import java.util.List;

import io.reark.reark.data.DataStreamNotification;
import ix.Ix;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.providers.ProgressStatusProvider;
import quickbeer.android.utils.StringUtils;
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
    private final DataLayer.GetBeerSearch getBeerSearch;

    @NonNull
    private final SearchViewViewModel searchViewViewModel;

    @NonNull
    private final ProgressStatusProvider progressStatusProvider;

    @NonNull
    private final PublishSubject<Observable<DataStreamNotification<ItemList<String>>>> source = PublishSubject.create();

    @NonNull
    private final PublishSubject<List<BeerViewModel>> beers = PublishSubject.create();

    protected BeerListViewModel(@NonNull DataLayer.GetBeer getBeer,
                                @NonNull DataLayer.GetBeerSearch getBeerSearch,
                                @NonNull SearchViewViewModel searchViewViewModel,
                                @NonNull ProgressStatusProvider progressStatusProvider) {
        this.getBeer = get(getBeer);
        this.getBeerSearch = get(getBeerSearch);
        this.searchViewViewModel = get(searchViewViewModel);
        this.progressStatusProvider = get(progressStatusProvider);
    }

    @NonNull
    protected abstract Observable<DataStreamNotification<ItemList<String>>> dataSource();

    @NonNull
    protected abstract Observable<DataStreamNotification<ItemList<String>>> reloadSource();

    protected boolean reportsProgress() {
        return true;
    }

    @NonNull
    public Observable<List<BeerViewModel>> getBeers() {
        return beers.asObservable();
    }

    public void reload() {
        source.onNext(reloadSource());
    }

    @Override
    protected void bind(@NonNull CompositeSubscription subscription) {
        // Switch source according to selector subject
        ConnectableObservable<DataStreamNotification<ItemList<String>>> sharedObservable =
                Observable.switchOnNext(source)
                        .subscribeOn(Schedulers.computation())
                        .publish();

        // Construct progress status. Completed with value means we'll receive onNext.
        subscription.add(sharedObservable
                .filter(notification -> !notification.isCompletedWithValue())
                .map(toProgressStatus())
                .startWith(ProgressStatus.LOADING)
                .distinctUntilChanged()
                .subscribe(this::setProgressStatus));

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
            subscription.add(progressStatusProvider
                    .addProgressObservable(sharedObservable
                    .map(notification -> notification)));
        }

        subscription.add(sharedObservable.connect());

        // Start with default data source
        source.onNext(dataSource());

        // Switch to search on new search term
        subscription.add(get(searchViewViewModel)
                .getQueryStream()
                .distinctUntilChanged()
                .filter(StringUtils::hasValue)
                .doOnNext(query -> Timber.d("query(%s)", query))
                .map(query -> get(getBeerSearch).call(query))
                .subscribe(source::onNext, Timber::e));
    }

    @NonNull
    private Func1<List<Integer>, List<BeerViewModel>> toBeerViewModelList() {
        return beerIds -> Ix.from(beerIds)
                .map(id -> new BeerViewModel(id, getBeer, progressStatusProvider))
                .toList();
    }

    @Override
    protected boolean hasValue(@Nullable ItemList<String> item) {
        return !get(item).getItems().isEmpty();
    }
}
